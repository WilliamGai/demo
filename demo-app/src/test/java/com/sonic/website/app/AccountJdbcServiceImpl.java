package com.sonic.website.app;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

import com.sonic.website.app.security.interfaces.AccountService;
import com.sonic.website.app.security.vo.RoleVO;
import com.sonic.website.app.security.vo.UserVO;
import com.sonic.website.core.common.support.LogCore;

@Service("accountJdbcService")
public class AccountJdbcServiceImpl implements AccountService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public static final UserWrapper USER_WRAPPER = new UserWrapper();

    public static class UserWrapper implements RowMapper<UserVO> {
        @Override
        public UserVO mapRow(ResultSet rs, int rowNum) throws SQLException {
            UserVO user = new UserVO();
            user.setName(rs.getString("name"));
            user.setPassword(rs.getString("password"));
            user.setStatus(rs.getByte("status"));
            return user;
        }
    }

    @Override
    public int saveOrUpdateUser(UserVO user) {
        String querySql = "REPLACE INTO t_user (NAME, PASSWORD) VALUES(?, ?)";
        Object[] args = new Object[] { user.getName(), user.getPassword()};
        return jdbcTemplate.update(querySql, args);
    }

    @Override
    public UserVO getUserByName(String name) {
        String querySql = "select * from t_user WHERE name =?";
        Object[] args = new Object[] { name };
        try {
            UserVO user = jdbcTemplate.queryForObject(querySql, args, USER_WRAPPER);
            LogCore.BASE.info("user:{}", user);
            return user;
        } catch (Exception e) {
            LogCore.BASE.error("getUserErr{}, {}", querySql, e);
            return null;
        }
    }

    @Override
    public Map<String, UserVO> getAllUsers() {
        Map<String, UserVO> map = new HashMap<>();
        List<UserVO> users = jdbcTemplate.query("select * from t_user", USER_WRAPPER);
        users.forEach(u -> map.put(u.getName(), u));
        return map;
    }

    @Override
    public void deleteUser(String name) {
        jdbcTemplate.update("DELETE FROM t_user    WHERE name = ?", new Object[] { name });
    }

    @Override
    public int saveOrUpdateRole(RoleVO role) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public RoleVO getRoleByName(String name) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void deleteRole(String name) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public Map<String, RoleVO> getAllRoles() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Boolean existUserByName(String name) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Boolean existRoleById(int id) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Boolean existRoleByName(String name) {
        // TODO Auto-generated method stub
        return null;
    }
}