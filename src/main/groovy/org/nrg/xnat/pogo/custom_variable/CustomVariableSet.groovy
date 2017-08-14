package org.nrg.xnat.pogo.custom_variable

import org.nrg.xnat.pogo.DataType
import org.nrg.xnat.pogo.Project
import org.nrg.xnat.util.ListUtils

class CustomVariableSet {

    DataType dataType
    boolean isProjectSpecific
    String name
    String description
    private final List<Project> projects = []
    private final List<CustomVariable> customVariables = []

    CustomVariableSet() {}

    List<Project> getProjects() {
        return projects
    }

    void setProjects(List<Project> projects) {
        ListUtils.copyInto(projects, this.projects)
    }

    List<CustomVariable> getCustomVariables() {
        return customVariables
    }

    void setCustomVariables(List<CustomVariable> customVariables) {
        ListUtils.copyInto(customVariables, this.customVariables)
    }

    CustomVariableSet projects(List<Project> projects) {
        setProjects(projects)
        return this
    }

    CustomVariableSet dataType(DataType dataType) {
        setDataType(dataType)
        return this
    }

    CustomVariableSet projectSpecific(boolean specific) {
        setIsProjectSpecific(specific)
        return this
    }

    CustomVariableSet name(String name) {
        setName(name)
        return this
    }

    CustomVariableSet description(String description) {
        setDescription(description)
        return this
    }

    CustomVariableSet customVariables(List<CustomVariable> customVariables) {
        setCustomVariables(customVariables)
        return this
    }

}
