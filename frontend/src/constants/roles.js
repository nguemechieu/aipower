export default class ROLES {
    static USER = 'USER';
    static ADMIN = 'ADMIN';
    static SUPER_ADMIN = 'SUPER_ADMIN';
    static EMPLOYEE = 'EMPLOYEE';
    static MANAGER = 'MANAGER';
    static EDITOR = 'EDITOR';
    static SUPERVISOR = 'SUPERVISOR';
    static OWNER = 'OWNER';
    static AUTHOR = 'AUTHOR';

    constructor(value) {
        this.value = value;
    }

    handleRoleChange = (event) => {
        this.value = event.target.value;
    }
    render() {
        return (
            <select value={this.value} onChange={this.handleRoleChange}>
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