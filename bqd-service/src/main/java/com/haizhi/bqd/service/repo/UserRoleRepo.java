package com.haizhi.bqd.service.repo;

import com.haizhi.bqd.service.model.UserRole;
import com.haizhi.bqd.service.support.TxManagerConstant;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by chenbo on 17/4/6.
 */
@Repository("UserRoleRepo")
public class UserRoleRepo {
    private static final String SELECT_BASE_FIELDS = "select id, tenant_id, role_id, user_id, status, cTime, uTime from USER_ROLE_USER ";
    private static final String SELECT_BY_ROLE_ID = SELECT_BASE_FIELDS + "where status = 0 and tenant_id = ? and role_id = ?";
    private static final String SELECT_ALL_BY_TENANT_ID = SELECT_BASE_FIELDS + "where status = 0 and tenant_id = ?";
    private static final String SELECT_BY_USER_ID = SELECT_BASE_FIELDS + "where status = 0 and tenant_id = ? and role_id = ? and user_id = ?";
    private static final String SELECT_BY_USER_ID2 = SELECT_BASE_FIELDS + "where status = 0 and tenant_id = ? and user_id = ?";
    private static final String CREATE_SQL = "insert into USER_ROLE_USER(tenant_id, role_id, user_id, status, cTime, uTime) VALUES (?,?,?,?,?,?)";
    private static final String UPDATE_SQL = "update USER_ROLE_USER set role_id = ?, user_id = ?, status = ?, uTime = ? where tenant_id = ? and id = ?";


    @Setter
    private JdbcTemplate template;

    private final RowMapper<UserRole> USER_ROLE_ROW_MAPPER = new BeanPropertyRowMapper<>(UserRole.class);

    public void persist(String tenantId, UserRole userRole) {
//        template.update(CREATE_SQL, new Object[]{
//                userRole.getRoleId(),
//                userRole.getUserId(),
//                userRole.getStatus(),
//                userRole.getcTime(),
//                userRole.getuTime()
//        });
    }

    public void update(String tenantId, UserRole userRole) {
//        template.update(UPDATE_SQL, new Object[]{
//                userRole.getRoleId(),
//                userRole.getUserId(),
//                userRole.getStatus(),
//                userRole.getuTime(),
//                userRole.getTenantId(),
//                userRole.getId()
//        });
    }


    
    public void deleteByRoleId(String tenantId, Long roleId) {
//        List<UserRole> userRoleList = findByCondition(tenantId, roleId);
//        if(CollectionUtils.isNotEmpty(userRoleList)){
//            for(UserRole userRole: userRoleList) {
//                userRole.setStatus(DataStatus.DELETE.ordinal());
//                userRole.setuTime(System.currentTimeMillis());
//
//                update(tenantId, userRole);
//            }
//        }
    }

    
    public void deleteUserRole(String tenantId, Long roleId, Long userId) {
//        UserRole userRole = template.queryForObject(SELECT_BY_USER_ID, new Object[]{tenantId, roleId, userId}, new UserRoleRowMapper());
//        if(userRole != null){
//            userRole.setStatus(DataStatus.DELETE.ordinal());
//            userRole.setuTime(System.currentTimeMillis());
//
//            update(tenantId, userRole);
//        }
    }

    @Transactional(TxManagerConstant.TxManager)
    public void deleteByUserId(String tenantId, Long userId) {
//        List<UserRole> userRoleList = getRolesByUserId(tenantId, userId);
//        if(CollectionUtils.isNotEmpty(userRoleList)){
//            for(UserRole userRole: userRoleList){
//                userRole.setStatus(DataStatus.DELETE.ordinal());
//                userRole.setuTime(System.currentTimeMillis());
//                update(tenantId, userRole);
//            }
//        }
    }

    
    public List<UserRole> findByCondition(String tenantId, Long roleId) {
//        List<UserRole> userRoleList = template.query(SELECT_BY_ROLE_ID, new Object[]{tenantId, roleId}, new UserRoleRowMapper());

        return null;
    }

    
    public List<UserRole> getRolesByUserId(String tenantId, Long userId) {
//        List<UserRole> userRoleList = template.query(SELECT_BY_USER_ID2, new Object[]{tenantId, userId}, new UserRoleRowMapper());

        return null;
    }

    
    public List<UserRole> getAllRoles(String tenantId) {
//        return template.query(SELECT_ALL_BY_TENANT_ID, new Object[]{tenantId}, new UserRoleRowMapper());
        return null;
    }

}
