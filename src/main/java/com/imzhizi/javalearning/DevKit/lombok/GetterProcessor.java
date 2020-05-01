package com.imzhizi.javalearning.DevKit.lombok;

import com.sun.source.tree.Tree;
import com.sun.tools.javac.api.JavacTrees;
import com.sun.tools.javac.code.Flags;
import com.sun.tools.javac.processing.JavacProcessingEnvironment;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.TreeMaker;
import com.sun.tools.javac.tree.TreeTranslator;
import com.sun.tools.javac.util.*;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import java.util.ArrayList;
import java.util.Set;

/**
 * created by zhizi
 * on 5/1/20 20:42
 */
@SupportedAnnotationTypes("*")
@SupportedSourceVersion(SourceVersion.RELEASE_8)
public class GetterProcessor extends AbstractProcessor {
    private JavacTrees javacTrees;
    private TreeMaker treeMaker;
    private Names names;
    private Messager messager;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        this.javacTrees = JavacTrees.instance(processingEnv);
        this.messager = processingEnv.getMessager();
        Context context = ((JavacProcessingEnvironment) processingEnv).getContext();
        this.treeMaker = TreeMaker.instance(context);
        this.names = Names.instance(context);
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        roundEnv.getElementsAnnotatedWith(MyGetter.class)
                .forEach(element -> javacTrees.getTree(element).accept(new TreeTranslator() {
                    @Override
                    public void visitClassDef(JCTree.JCClassDecl jcClassDecl) {
                        ArrayList<JCTree.JCVariableDecl> list = new ArrayList<>();
                        jcClassDecl.defs.forEach(def -> {
                            if (def.getKind().equals(Tree.Kind.VARIABLE)) {
                                list.add((JCTree.JCVariableDecl) def);
                                messager.printMessage(Diagnostic.Kind.NOTE, "detect field " + ((JCTree.JCVariableDecl) def).getName().toString());
                            }
                        });

                        list.forEach(decl -> jcClassDecl.defs = jcClassDecl.defs.append(addGetterMethod(decl)));
                        super.visitClassDef(jcClassDecl);
                    }
                }));
        return true;
    }

    private JCTree addGetterMethod(JCTree.JCVariableDecl decl) {
        ListBuffer<JCTree.JCStatement> statements = new ListBuffer<>();
        statements.append(
                treeMaker.Return(
                        treeMaker.Select(
                                treeMaker.Ident(names.fromString("this")),
                                decl.getName())));

        return treeMaker.MethodDef(
                treeMaker.Modifiers(Flags.PUBLIC),
                getName(decl.getName().toString()),
                decl.vartype,
                List.nil(),
                List.nil(),
                List.nil(),
                treeMaker.Block(0, statements.toList()),
                null
        );
    }

    private Name getName(String name) {
        return names.fromString("get" + name.substring(0, 1).toUpperCase() + name.substring(1));
    }
}
