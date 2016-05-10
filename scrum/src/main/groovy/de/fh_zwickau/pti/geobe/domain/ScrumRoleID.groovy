//package de.fh_zwickau.pti.geobe.domain;
//
//import javax.persistence.Embeddable;
//import java.io.Serializable;
//
///**
// * Created by Heliosana on 14.04.2016.
// */
//
//@Embeddable
//public class ScrumRoleID implements Serializable {
//
//    Long project_id;
//    Long scrumUser_id;
//    ROLETYPE userRole;
//
//
//    public ScrumRoleID(Long project_id, Long scrumUser_id, ROLETYPE userRole) {
//        super();
//        this.project_id = project_id;
//        this.scrumUser_id = scrumUser_id;
//        this.userRole = userRole;
//    }
//
//    public ScrumRoleID() {}
//
//    public Long getProjectId() {
//        return project_id;
//    }
//
//    public Long getScrumUserId() {
//        return scrumUser_id;
//    }
//
//    public ROLETYPE getUserRole() {
//        return userRole;
//    }
//
//    @Override
//    public boolean equals(Object obj) {
//        if (obj instanceof ScrumRoleID) {
//            ScrumRoleID cast = (ScrumRoleID) obj;
//            return (cast.project_id == project_id && cast.scrumUser_id == scrumUser_id && cast.userRole.equals(userRole));
//        } else return false;
//    }
//    public int hashCode() {
//        return project_id.hashCode() + scrumUser_id.hashCode() + userRole.hashCode();
//    }
//
//    public void setUserRole(ROLETYPE userRole) {
//        this.userRole = userRole;
//    }
//
//    public String toString() {
//        return "roleID: " + project_id + "\t" + scrumUser_id + "\t" + userRole;
//    }
//}
