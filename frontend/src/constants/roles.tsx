import React, { Component } from "react";

// Define the ROLES enum
export enum ROLES {
    USER = "USER",
    ADMIN = "ADMIN",
    SUPER_ADMIN = "SUPER_ADMIN",
    EMPLOYEE = "EMPLOYEE",
    MANAGER = "MANAGER",
    EDITOR = "EDITOR",
    SUPERVISOR = "SUPERVISOR",
    OWNER = "OWNER",
}



// Define the type for the component state
interface RoleSelectorState {
    value: ROLES;
}

export default class RoleSelector extends Component<{
    role: ROLES
}, RoleSelectorState> {
    static ADMIN: string;
    static USER: string;
    // Initialize the state with a default role
    constructor(props:{
        role: ROLES
    }) {
        super(props);
        this.state = { value: ROLES.USER };
    }

    // Handle the role change from the select dropdown
    handleRoleChange = (event: React.ChangeEvent<HTMLSelectElement>) => {
        this.setState({ value: event.target.value as ROLES });
    };

    render() {
        return (
            <select value={this.state.value} onChange={this.handleRoleChange}>
                <option value={ROLES.USER}>User</option>
                <option value={ROLES.ADMIN}>Admin</option>
                <option value={ROLES.SUPER_ADMIN}>Super Admin</option>
                <option value={ROLES.EMPLOYEE}>Employee</option>
                <option value={ROLES.MANAGER}>Manager</option>
                <option value={ROLES.EDITOR}>Editor</option>
                <option value={ROLES.SUPERVISOR}>Supervisor</option>
                <option value={ROLES.OWNER}>Owner</option>
            </select>
        );
    }
}
