/** 
 *  Generated by OpenJPA MetaModel Generator Tool.
**/

package com.x.report.core.entity;

import com.x.base.core.entity.SliceJpaObject_;
import java.lang.Integer;
import java.lang.String;
import javax.persistence.metamodel.SingularAttribute;

@javax.persistence.metamodel.StaticMetamodel
(value=com.x.report.core.entity.Report_I_Ext_Content.class)
@javax.annotation.Generated
(value="org.apache.openjpa.persistence.meta.AnnotationProcessor6",date="Thu Dec 27 11:41:25 CST 2018")
public class Report_I_Ext_Content_ extends SliceJpaObject_  {
    public static volatile SingularAttribute<Report_I_Ext_Content,String> id;
    public static volatile SingularAttribute<Report_I_Ext_Content,String> infoLevel;
    public static volatile SingularAttribute<Report_I_Ext_Content,Integer> orderNumber;
    public static volatile SingularAttribute<Report_I_Ext_Content,String> profileId;
    public static volatile SingularAttribute<Report_I_Ext_Content,String> reportId;
    public static volatile SingularAttribute<Report_I_Ext_Content,String> targetPerson;
}