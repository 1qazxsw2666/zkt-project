/*
 * Copyright(C), 2015-2018, Beifeng
 * FileName: MenuMapper
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * Email Address: liwei@ibeifeng.com
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.zkt.project.admin.mapper;

import com.zkt.project.admin.entity.SysMenu;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/**
 * @author liwei
 * @create 2018/10/16 0016
 */
public interface MenuMapper extends Mapper<SysMenu> {


    /**
     * 根据用户和组的权限关系查找用户可访问菜单
     * @param userId
     * @return
     */
    List<SysMenu> selectAuthorityMenuByUserId (@Param("userId") String userId);

    @Select("select count(1) from sys_menu where code = #{code}")
    int checkByCode(@Param("code")String code);

    int checkCountUserAuth(@Param("uri")String uri,@Param("userId")String userId);

    List<SysMenu> getGroupAuth(String groupId);

    @Delete("delete from sys_group_authority where group_id = #{groupId}")
    void deleteGroupAuth(@Param("groupId")String groupId);

    @Insert("insert into sys_group_authority(group_id,menu_id) values(#{groupId} ,#{menuId})")
    void insertGroupAuth(@Param("groupId")String groupId,@Param("menuId") String menuId);
}
