
import React from 'react';

import { useEffect, useState } from "react";
import axios from "axios";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faEdit, faTrash } from "@fortawesome/free-solid-svg-icons";
import {axiosPrivate} from "../api/axios";

const API_URL = "/api/v3/users";

const AdminUserManagement = () => {
    const [users, setUsers] = useState([]);
    const [error, setError] = useState("");
    const [loading, setLoading] = useState(true);
    const [editingUser, setEditingUser] = useState(null);

    useEffect(() => {
        fetchUsers().then(r =>
        {
            console.log("Request completed successfully ");
            setUsers(r.data);
            setLoading(false);
        }).catch(error =>
        {
            console.error("Failed to fetch users", error);
            setError("Failed to fetch users \n"+ error);
        });
    }, []);

    const fetchUsers = async () => {
        setLoading(true);
        try {
           await axiosPrivate.get(API_URL).then(
               response => setUsers(response.data)
           )

        } catch (err) {
            setError(err.message);
        } finally {
            setLoading(false);
        }
    };

    const handleDeleteUser = async (userId) => {
        try {
            await axios.delete(`${API_URL}/${userId}`);
            setUsers(users.filter((user) => user.id !== userId));
        } catch (err) {
            setError("Error deleting user");
        }
    };

    const handleEnableDisableUser = async (user) => {
        try {
            const updatedUser = { ...user, enabled: !user.enabled };
            await axiosPrivate.patch(`${API_URL}/update/id:${user.id}`, updatedUser);
            setUsers(users.map((u) => (u.id === user.id ? updatedUser : u)));
        } catch (err) {
            setError("Error updating user status");
        }
    };

    const handleRoleUpdate = async (user, newRole) => {
        try {
            const updatedUser = { ...user, role: newRole };
            await axios.patch(`${API_URL}/${user.id}`, updatedUser);
            setUsers(users.map((u) => (u.id === user.id ? updatedUser : u)));
        } catch (err) {
            setError("Error updating user role");
        }
    };

    const handleEditUser = (user) => {
        setEditingUser(user);
    };

    const handleSaveEdit = async () => {
        try {
            await axiosPrivate.patch(`${API_URL}/update:id${editingUser.id}`, editingUser);
            setUsers(users.map((user) => (user.id === editingUser.id ? editingUser : user)));
            setEditingUser(null);
        } catch (err) {
            setError("Error saving user details"+err.message);
        }
    };

    if (loading) return <p>Loading users...</p>;
    if (error) return <p className="error">{error}</p>;

    return (
        <div className="admin-user-management">
            <h1>User Management</h1>

            {editingUser ? (
                <div className="edit-user-modal">
                    <h2>Edit User: {editingUser.username}</h2>
                    <label>
                        Username:
                        <input
                            type="text"
                            value={editingUser.username}
                            onChange={(e) => setEditingUser({ ...editingUser, username: e.target.value })}
                        />
                    </label>
                    <label>
                        Role:
                        <select
                            value={editingUser.role}
                            onChange={(e) => setEditingUser({ ...editingUser, role: e.target.value })}
                        >
                            <option value="USER">User</option>
                            <option value="ADMIN">Admin</option>
                        </select>
                    </label>
                    <label>
                        Enabled:
                        <input
                            type="checkbox"
                            checked={editingUser.enabled}
                            onChange={(e) => setEditingUser({ ...editingUser, enabled: e.target.checked })}
                        />
                    </label>
                    <button onClick={handleSaveEdit}>Save</button>
                    <button onClick={() => setEditingUser(null)}>Cancel</button>
                </div>
            ) : (
                <table>
                    <thead>
                    <tr>
                        <th>Username</th>
                        <th>Email</th>
                        <th>Role</th>
                        <th>Enabled</th>
                        <th>Actions</th>
                    </tr>
                    </thead>
                    <tbody>
                    {users.map((user) => (
                        <tr key={user.id}>
                            <td>{user.username}</td>
                            <td>{user.email}</td>
                            <td>
                                <select
                                    value={user.role}
                                    onChange={(e) => handleRoleUpdate(user, e.target.value)}
                                >
                                    <option value="USER">User</option>
                                    <option value="ADMIN">Admin</option>
                                </select>
                            </td>
                            <td>
                                <input
                                    type="checkbox"
                                    checked={user.enabled}
                                    onChange={() => handleEnableDisableUser(user)}
                                />
                            </td>
                            <td>
                                <button onClick={() => handleEditUser(user)}>
                                    <FontAwesomeIcon icon={faEdit} /> Edit
                                </button>
                                <button onClick={() => handleDeleteUser(user.id)}>
                                    <FontAwesomeIcon icon={faTrash} /> Delete
                                </button>
                            </td>
                        </tr>
                    ))}
                    </tbody>
                </table>
            )}
        </div>
    );
};

export default AdminUserManagement;
