package com.powerwin.dao;

import com.powerwin.entity.CpcDay;

import java.util.List;

public interface CpcDayMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table cpc_day
     *
     * @mbg.generated Wed Jul 11 11:57:48 CST 2018
     */
    int deleteByPrimaryKey(Integer id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table cpc_day
     *
     * @mbg.generated Wed Jul 11 11:57:48 CST 2018
     */
    int insert(CpcDay record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table cpc_day
     *
     * @mbg.generated Wed Jul 11 11:57:48 CST 2018
     */
    CpcDay selectByPrimaryKey(Integer id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table cpc_day
     *
     * @mbg.generated Wed Jul 11 11:57:48 CST 2018
     */
    List<CpcDay> selectAll();

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table cpc_day
     *
     * @mbg.generated Wed Jul 11 11:57:48 CST 2018
     */
    int updateByPrimaryKey(CpcDay record);
}