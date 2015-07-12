package se.emilsjolander.intentbuilder;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;

public class Processor extends AbstractProcessor {

    public static Processor instance;

    public Types typeUtils;
    public Elements elementUtils;
    public Filer filer;
    public Messager messager;

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        return new HashSet<String>() {{
            add(se.emilsjolander.intentbuilder.IntentBuilder.class.getCanonicalName());
        }};
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        instance = this;
        typeUtils = processingEnv.getTypeUtils();
        elementUtils = processingEnv.getElementUtils();
        filer = processingEnv.getFiler();
        messager = processingEnv.getMessager();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        for (Element annotatedElement : roundEnv.getElementsAnnotatedWith(se.emilsjolander.intentbuilder.IntentBuilder.class)) {
            // Make sure element is a field or a method declaration
            if (!annotatedElement.getKind().isClass()) {
                error(annotatedElement, "Only classes can be annotated with @%s", IntentBuilder.class.getSimpleName());
                return true;
            }

            try {
                TypeSpec builderSpec = getBuilderSpec(annotatedElement);
                JavaFile builderFile = JavaFile.builder(getPackageName(annotatedElement), builderSpec).build();
                builderFile.writeTo(filer);
            } catch (Exception e) {
                error(annotatedElement, "Could not create intent builder for %s: %s", annotatedElement.getSimpleName(), e.getMessage());
            }
        }
        return true;
    }

    private void error(Element e, String msg, Object... args) {
        messager.printMessage(Diagnostic.Kind.ERROR, String.format(msg, args), e);
    }

    private String getPackageName(Element e) {
        while (!(e instanceof PackageElement)) {
            e = e.getEnclosingElement();
        }
        return ((PackageElement)e).getQualifiedName().toString();
    }

    private TypeSpec getBuilderSpec(Element annotatedElement) {
        List<Element> required = new ArrayList<>();
        List<Element> optional = new ArrayList<>();
        List<Element> all = new ArrayList<>();

        getAnnotatedFields(annotatedElement, required, optional);
        all.addAll(required);
        all.addAll(optional);

        final String name = String.format("%sIntentBuilder", annotatedElement.getSimpleName());
        TypeSpec.Builder builder = TypeSpec.classBuilder(name)
            .addModifiers(Modifier.PUBLIC, Modifier.FINAL);

        MethodSpec.Builder constructor = MethodSpec.constructorBuilder()
                .addModifiers(Modifier.PUBLIC);
        for (Element e : required) {
            String paramName = getParamName(e);
            builder.addField(TypeName.get(e.asType()), paramName, Modifier.PRIVATE, Modifier.FINAL);
            constructor.addParameter(TypeName.get(e.asType()), paramName);
            constructor.addStatement("this.$N = $N", paramName, paramName);
        }
        builder.addMethod(constructor.build());

        for (Element e : optional) {
            String paramName = getParamName(e);
            builder.addField(TypeName.get(e.asType()), paramName, Modifier.PRIVATE);
            builder.addMethod(MethodSpec.methodBuilder(paramName)
                    .addModifiers(Modifier.PUBLIC)
                    .addParameter(TypeName.get(e.asType()), paramName)
                    .addStatement("this.$N = $N", paramName, paramName)
                    .addStatement("return this")
                    .returns(ClassName.get(getPackageName(annotatedElement), name))
                    .build());
        }

        MethodSpec.Builder buildMethod = MethodSpec.methodBuilder("build")
                .addModifiers(Modifier.PUBLIC)
                .addParameter(Context.class, "context")
                .addStatement("$T intent = new Intent(context, $T.class)", Intent.class, TypeName.get(annotatedElement.asType()));
        for (Element e : all) {
            String paramName = getParamName(e);
            buildMethod.addStatement("intent.putExtra($S, $N)", paramName, paramName);
        }
        buildMethod.returns(Intent.class)
                .addStatement("return intent");
        builder.addMethod(buildMethod.build());

        MethodSpec.Builder injectMethod = MethodSpec.methodBuilder("inject")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .addParameter(Intent.class, "intent")
                .addParameter(TypeName.get(annotatedElement.asType()), "activity")
                .addStatement("$T extras = intent.getExtras()", Bundle.class);
        for (Element e : all) {
            String paramName = getParamName(e);
            injectMethod.beginControlFlow("if (extras.containsKey($S))", paramName)
                .addStatement("activity.$N = ($T) extras.get($S)", e.getSimpleName().toString(), e.asType(), paramName)
                .nextControlFlow("else")
                .addStatement("activity.$N = null", e.getSimpleName().toString())
                .endControlFlow();
        }
        builder.addMethod(injectMethod.build());

        return builder.build();
    }

    private String getParamName(Element e) {
        String extraValue = e.getAnnotation(Extra.class).value();
        return extraValue !=null && !extraValue.trim().isEmpty() ? extraValue : e.getSimpleName().toString();
    }

    private void getAnnotatedFields(Element annotatedElement, List<Element> required, List<Element> optional) {
        for (Element e : annotatedElement.getEnclosedElements()) {
            if (e.getAnnotation(Extra.class) != null) {
                if (hasAnnotation(e, "Nullable")) {
                    optional.add(e);
                } else {
                    required.add(e);
                }
            }
        }

        List<? extends TypeMirror> superTypes = Processor.instance.typeUtils.directSupertypes(annotatedElement.asType());
        TypeMirror superClassType = superTypes.size() > 0 ? superTypes.get(0) : null;
        Element superClass = superClassType == null ? null : Processor.instance.typeUtils.asElement(superClassType);
        if (superClass != null && superClass.getKind() == ElementKind.CLASS) {
            getAnnotatedFields(superClass, required, optional);
        }
    }

    private boolean hasAnnotation(Element e, String name) {
        for (AnnotationMirror annotation : e.getAnnotationMirrors()) {
            if (annotation.getAnnotationType().asElement().getSimpleName().toString().equals(name)) {
                return true;
            }
        }
        return false;
    }

}