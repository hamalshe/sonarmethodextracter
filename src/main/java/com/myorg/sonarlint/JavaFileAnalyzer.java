package com.myorg.sonarlint;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.MethodDeclaration;

import com.myorg.common.utils.UtilityFunctions;

public class JavaFileAnalyzer {
    private static  JavaFileAnalyzer INSTANCE = null;
    final Map<String, List<MethodInfo>> methodList = new LinkedHashMap<String, List<MethodInfo>>();
    private static final Object lock = new Object();

    private JavaFileAnalyzer(){
    }

    public static JavaFileAnalyzer getInstance(){
        synchronized (lock){
            if (JavaFileAnalyzer.INSTANCE == null){
                JavaFileAnalyzer.INSTANCE = new JavaFileAnalyzer();
            }
        }
        return JavaFileAnalyzer.INSTANCE;
    }

    public void parseJavaFile(final String baseDir, final String fileName) {
        if(!methodList.containsKey (fileName) ){
            ASTParser parser = ASTParser.newParser(AST.JLS3);
            try {
                parser.setSource(FileUtils.readFileToString(new File(baseDir, fileName), "UTF-8").toCharArray());
            } catch (IOException e) {
                e.printStackTrace() ;
            }
            parser.setKind(ASTParser.K_COMPILATION_UNIT);
            parser.setResolveBindings(true) ;

            Map options = JavaCore.getOptions();
            JavaCore.setComplianceOptions(JavaCore.VERSION_1_6, options) ;
            parser.setCompilerOptions(options) ;

            final CompilationUnit cu = (CompilationUnit) parser.createAST(null);

            cu.accept(new ASTVisitor() {
                @Override
                public boolean visit(MethodDeclaration node) {
                    StringBuilder methodNameBuilder = new StringBuilder();
                    if (UtilityFunctions.isNotNullAndNotEmpty (node.modifiers())) {
                        String modifier = node.modifiers().toString().replace(",", "").replace("[", "").replace("]", "");
                        methodNameBuilder.append(modifier);
                    }

                    if (UtilityFunctions.isNotNullAndNotEmpty(node.getReturnType2())) {
                        methodNameBuilder.append(" ").append(node.getReturnType2());
                    }
                    
                    if(UtilityFunctions.isNotNullAndNotEmpty(node.getName().toString())) {
                        methodNameBuilder.append(" ").append(node.getName().toString());
                    }
                    methodNameBuilder.append("(");
                    if(UtilityFunctions.isNotNullAndNotEmpty(node.parameters())) {
                        String parameters = node.parameters().toString().replace("[", "").replace("]", "");
                        methodNameBuilder.append(parameters);
                    }
                    methodNameBuilder.append(")");
                    
                    if(UtilityFunctions.isNotNullAndNotEmpty(node.thrownExceptions())) {
                        String exceptions = node.thrownExceptions().toString().replace("[", "").replace("]", "");
                        methodNameBuilder.append(" throws ").append(exceptions);
                    }
                    final String methodBody = node.toString();
                    //System.out.println(methodNameBuilder.toString());
                    //System.out.println("methodBody = "+methodBody);
                    //System.out.println("methodStartBuilder = "+methodStartBuilder.toString());
                    try{
                        final String methodName = methodNameBuilder.toString().trim();
                        //System.out.println("MethodName: " + methodName);
                        final int startLine = cu.getLineNumber(node.getName().getStartPosition());
                        //System.out.println("MethodLineNumber Start: " + startLine);
                        final int endLine = cu.getLineNumber(node.getStartPosition() + node.getLength());
                        //System.out.println("MethodLineNumber End: " + endLine);
                        MethodInfo methodInfo = new MethodInfo(methodName, startLine, endLine);
                        List<MethodInfo> methodInfos = methodList.get(fileName);
                        if (UtilityFunctions.isNullOrEmpty(methodInfos)) {
                            methodInfos = new ArrayList<MethodInfo>();
                        }
                        methodInfos.add(methodInfo);
                        methodList.put(fileName, methodInfos);

                    }catch (StringIndexOutOfBoundsException stre) {
                        stre.printStackTrace();
                        System.out.println("methodBody = "+methodBody);
                    }
                    return false;
                }
            });
        }
    }

    public void printAllMapEntries() {
        for (Map.Entry<String, List<MethodInfo>> objEntry : methodList.entrySet()) {
            System.out.println("key :: "+objEntry.getKey());
            for (MethodInfo methodInfo : objEntry.getValue()) {
                System.out.println("methodInfo :: "+methodInfo.toString());
            }
        }
    }

    public String findMethodName(String fileName, Integer lineNum) {
        String methodName = null;
        if(lineNum == null || lineNum <= 0) {
            return "";
        }

        List<MethodInfo> methodInfos = methodList.get(fileName);
        if (UtilityFunctions.isNullOrEmpty(methodInfos)) {
            return "";
        }
        for (MethodInfo methodInfo : methodInfos){
            if (methodInfo.getStartLine() <= lineNum
            && methodInfo.getEndLine() >= lineNum){
                methodName = methodInfo.getMethodName();
                break;
            }
        }
        return methodName;
    }

    public static void main(String[] args) {
        JavaFileAnalyzer javaFileAnalyzer = JavaFileAnalyzer.getInstance();
        javaFileAnalyzer.parseJavaFile("D:/JavaDesignPatterns/", "AdapterPattern/src/com/buildappswithpaulo/com/model/SocketObjectAdapterImplementation.java");
        javaFileAnalyzer.printAllMapEntries();
    }
}
