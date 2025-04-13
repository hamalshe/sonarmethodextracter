package com.myorg.sonarlint;

public class MethodInfo {

    private String methodName;
    private int startLine;
    private int endLine;

    public MethodInfo(String methodName, int startLine, int endLine) {
        this.methodName = methodName;
        this.startLine = startLine;
        this.endLine = endLine;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public int getStartLine() {
        return startLine;
    }

    public void setStartLine(int startLine) {
        this.startLine = startLine;
    }

    public int getEndLine() {
        return endLine;
    }

    public void setEndLine(int endLine) {
        this.endLine = endLine;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("MethodInfo{");
        sb.append("methodName='").append (methodName).append('\'');
        sb.append(", startLine=").append(startLine);
        sb.append(", endLine=").append(endLine);
        sb.append('}');
        return sb.toString();
    }
}