package com._54year.dawn2.generator;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

/**
 * 打印文件夹目录结构
 *
 * @author Andersen
 */
public class DirectoryTree {

    public static void main(String[] args) {
        // 获取当前工作目录
        File currentDir = new File(System.getProperty("user.dir"));
        System.out.println(currentDir.getAbsolutePath());
        // 指定要输出的层级
        int maxDepth = 2; // 可以根据需要调整层级

        // 指定需要排除的文件和目录名
        Set<String> excludedFilesAndDirectories = new HashSet<>();
        excludedFilesAndDirectories.add("folder1"); // 排除"folder1"目录
        excludedFilesAndDirectories.add("file1.txt"); // 排除"file1.txt"文件
        excludedFilesAndDirectories.add(".gradle");
        excludedFilesAndDirectories.add(".idea");
        excludedFilesAndDirectories.add(".git");
        excludedFilesAndDirectories.add(".gitignore");
        excludedFilesAndDirectories.add("out");
        excludedFilesAndDirectories.add("build");
        excludedFilesAndDirectories.add("gradle");
        excludedFilesAndDirectories.add("src");
        excludedFilesAndDirectories.add("codeout");
        // 可以添加更多的文件或目录名
        // excludedFilesAndDirectories.add("anotherFileOrDirectory");

        // 打印目录树
        printDirectoryTree(currentDir, "", 0, maxDepth, excludedFilesAndDirectories);
    }

    public static void printDirectoryTree(File dir, String indent, int currentDepth, int maxDepth, Set<String> excludedFilesAndDirectories) {
        // 获取目录下的所有文件和文件夹
        File[] files = dir.listFiles();
        if (files != null && currentDepth < maxDepth) {
            for (int i = 0; i < files.length; i++) {
                File file = files[i];

                // 如果是被排除的文件或目录，则跳过
                if (excludedFilesAndDirectories.contains(file.getName())) {
                    continue;
                }
                //如果是文件则跳过 不打印
                if (file.isFile()) {
                    continue;
                }

                // 判断是最后一个文件或文件夹
                boolean isLast = (i == files.length - 1);

                // 打印文件或文件夹的名称
                System.out.print(indent + (isLast ? "└── " : "├── ") + file.getName());

                // 如果是目录，递归调用
                if (file.isDirectory()) {
                    System.out.println(); // 新的一行
                    printDirectoryTree(file, indent + (isLast ? "    " : "│   "), currentDepth + 1, maxDepth, excludedFilesAndDirectories);
                } else {
                    System.out.println(); // 新的一行
                }
            }
        }
    }
}
