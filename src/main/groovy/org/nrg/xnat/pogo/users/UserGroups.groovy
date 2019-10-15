package org.nrg.xnat.pogo.users

class UserGroups {

    public static final UserGroup OWNER = new OwnerGroup()
    public static final UserGroup MEMBER = new MemberGroup()
    public static final UserGroup COLLABORATOR = new CollaboratorGroup()
    public static final List<UserGroup> STANDARD_GROUPS = [OWNER, MEMBER, COLLABORATOR]

}
