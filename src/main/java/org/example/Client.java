package org.example;
import org.example.Database.DatabaseDAO;
import org.example.Iterator.ComponentIterator;
import org.example.compositePattern.Component;

import java.io.IOException;
import java.sql.SQLException;

public class Client {
    public static void main(String[] args) {
        try {
            DatabaseDAO dao = new DatabaseDAO();

            // 获取指定机构下的所有用户及权限 (以“技术部”为例，orgId = "0101")
            System.out.println("\n技术部下的用户和权限：");
            ComponentIterator techDeptIterator = dao.getOrganizationIterator("010204");  // 直接获取技术部组织的迭代器
            while (techDeptIterator.hasNext()) {
                techDeptIterator.next();  // 迭代并输出用户和权限信息
            }

            // 获取指定用户的GUID和权限 (以用户名 "用户名" 为例)
            System.out.println("\n用户权限：");
            String fname="202225220615";
            System.out.println("用户"+fname+"(GUID):"+dao.getGUID(fname)+"\n拥有权限如下：");
            ComponentIterator userPermissionsIterator = dao.getUserPermissionsIteratorByFname(fname);
            while (userPermissionsIterator.hasNext()) {
                Component component = userPermissionsIterator.next();  // 获取权限字符串
                if (component != null) {
                    System.out.println(userPermissionsIterator.next());  // 迭代并输出用户权限
                }
            }

            dao.close();  // 关闭数据库连接
        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }
    }
}