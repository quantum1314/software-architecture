package org.example.Database;

import org.example.Iterator.ComponentIterator;
import org.example.Iterator.PermissionIterator;
import org.example.Iterator.UserIterator;
import org.example.compositePattern.Component;
import org.example.compositePattern.Organization;
import org.example.compositePattern.User;

import java.io.IOException;
import java.sql.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;

public class DatabaseDAO {
    private Connection conn;
    private Map<String, String> queries;

    public DatabaseDAO() throws SQLException, IOException {
        // 加载配置文件
        Config config = Config.loadConfig("D:\\SCAU\\我的大学之旅\\start\\2024\\下学期\\软体\\11.14\\txjg\\txjg\\src\\main\\resources\\db_config.yaml");

        // 获取数据库连接信息
        String url = config.getDatabaseConfig().getUrl();
        String username = config.getDatabaseConfig().getUsername();
        String password = config.getDatabaseConfig().getPassword();
        conn = DriverManager.getConnection(url, username, password);

        // 获取 SQL 查询语句
        queries = config.getQueries();
    }

    // 获取某个机构下的用户迭代器
    public ComponentIterator getOrganizationIterator(String orgId) throws SQLException {
        List<User> usersInOrg = getUsersByOrganization(orgId);
        return new UserIterator(usersInOrg);  // 传递用户列表
    }


    // 获取某个用户的权限迭代器（根据用户名）
    public ComponentIterator getUserPermissionsIteratorByFname(String fname) throws SQLException {
        List<String> permissions = getUserPermissionsByFname(fname);
        return new PermissionIterator(permissions);  // 传递权限列表
    }


    public String getGUID(String fname)
    {
        String queryUserGUID = queries.get("getUserGUIDByFname"); // 获取用户GUID的查询语句
        // 根据用户名获取用户GUID
        String userGUID = "";
        try (PreparedStatement stmt = conn.prepareStatement(queryUserGUID)) {
            stmt.setString(1, fname);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                userGUID = rs.getString("fUserGUID");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return userGUID;
    }

    // 获取用户权限（根据用户名）
    private List<String> getUserPermissionsByFname(String fname) throws SQLException {
        List<String> permissions = new ArrayList<>();
        String queryUserGUID = queries.get("getUserGUIDByFname"); // 获取用户GUID的查询语句
        String queryUserOrg = queries.get("getUserOrgIDs"); // 获取用户机构ID的查询语句
        String queryPermissions = queries.get("loadUserPermissions"); // 获取用户权限的查询语句
        String queryOrgName = queries.get("loadOrganizationName"); // 获取机构名字的查询语句

        // 根据用户名获取用户GUID
        String userGUID = "";
        try (PreparedStatement stmt = conn.prepareStatement(queryUserGUID)) {
            stmt.setString(1, fname);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                userGUID = rs.getString("fUserGUID");
            }
        }

        if (!userGUID.isEmpty()) {
            // 根据GUID获取用户机构ID
            String orgIDs = "";
            try (PreparedStatement stmt = conn.prepareStatement(queryUserOrg)) {
                stmt.setString(1, userGUID);
                ResultSet rs = stmt.executeQuery();

                if (rs.next()) {
                    orgIDs = rs.getString("fOrgIDs");
                }
            }

            if (!orgIDs.isEmpty()) {
                String[] orgIdArray = orgIDs.split("\\|");
                for (String orgId : orgIdArray) {
                    if (!orgId.trim().isEmpty()) {
                        String trimmedOrgId = orgId.replaceFirst("^0+", "");
                        try (PreparedStatement stmt = conn.prepareStatement(queryPermissions)) {
                            stmt.setString(1, trimmedOrgId);
                            ResultSet rs = stmt.executeQuery();

                            if (rs.next()) {
                                String permission = rs.getString("fPermission");
                                // 根据机构ID获取机构名字
                                String orgName = getOrganizationName(trimmedOrgId);
                                permissions.add(orgName + "（" + trimmedOrgId + "）权限: " + permission);
                            }
                        }
                    }
                }
            }
        }
        return permissions;
    }

    // 根据机构ID获取机构名字
    private String getOrganizationName(String orgId) throws SQLException {
        String orgName = "";
        try (PreparedStatement stmt = conn.prepareStatement(queries.get("loadOrganizationName"))) {
            stmt.setString(1, orgId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                orgName = rs.getString("fName");
            }
        }
        return orgName;
    }
    // 获取某个机构下的所有用户
    public List<User> getUsersByOrganization(String orgId) throws SQLException {
        List<User> users = new ArrayList<>();
        String query = queries.get("loadUsersByOrganization");

        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, "%" + orgId + "%");
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                String name = rs.getString("fName");
                String guid = rs.getString("fUserGUID");
                User user = new User(name, guid);
                users.add(user);
            }
        }
        return users;
    }

    // 加载组织树
    public Component loadOrganizationTree() throws SQLException {
        return loadOrganization("1");  // 假设从根组织开始加载
    }

    private Organization loadOrganization(String orgId) throws SQLException {
        Organization organization = null;

        String orgQuery = queries.get("loadOrganizationTree");
        try (PreparedStatement stmt = conn.prepareStatement(orgQuery)) {
            stmt.setString(1, orgId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String id = rs.getString("fID");
                String name = rs.getString("fName");
                String permissions = rs.getString("fPermission");
                organization = new Organization(id, name, permissions);
            }
        }

        if (organization != null) {
            // 加载子组织
            String childOrgQuery = queries.get("loadChildOrganizations");
            try (PreparedStatement stmt = conn.prepareStatement(childOrgQuery)) {
                stmt.setString(1, "%" + orgId + "%");
                ResultSet rs = stmt.executeQuery();

                while (rs.next()) {
                    String childOrgId = rs.getString("fID");
                    organization.add(loadOrganization(childOrgId));
                }
            }

            // 加载用户
            String userQuery = queries.get("loadUsersByOrganization");
            try (PreparedStatement stmt = conn.prepareStatement(userQuery)) {
                stmt.setString(1, "%" + orgId + "%");
                ResultSet rs = stmt.executeQuery();

                while (rs.next()) {
                    String userName = rs.getString("fName");
                    String userGUID = rs.getString("fUserGUID");
                    User user = new User(userName, userGUID);
                    organization.add(user);
                }
            }
        }

        return organization;
    }
    public void close() throws SQLException {
        if (conn != null) {
            conn.close();
        }
    }
}
