import React, { useState } from "react";
import "./Settings.css";

const Settings = () => {
    const [username, setUsername] = useState("JohnDoe123");
    const [email, setEmail] = useState("johndoe@example.com");
    const [password, setPassword] = useState("");
    const [theme, setTheme] = useState("light");
    const [twoFactor, setTwoFactor] = useState(false);

    // Notification preferences
    const [emailNotifications, setEmailNotifications] = useState(true);
    const [smsNotifications, setSmsNotifications] = useState(false);
    const [pushNotifications, setPushNotifications] = useState(true);

    const handleSaveProfile = () => {
        alert("Profile updated successfully!");
    };

    const handleSaveSecurity = () => {
        alert("Security settings updated successfully!");
    };

    const handleSavePreferences = () => {
        alert("Preferences updated successfully!");
    };

    const handleSaveNotifications = () => {
        alert("Notification preferences updated successfully!");
    };

    return (
        <div className="settings-container">
            <h2>Settings</h2>

            {/* Profile Section */}
            <section className="settings-section">
                <h3>Profile</h3>
                <div className="form-group">
                    <label htmlFor="username">Username</label>
                    <input
                        id="username"
                        type="text"
                        value={username}
                        onChange={(e) => setUsername(e.target.value)}
                    />
                </div>
                <div className="form-group">
                    <label htmlFor="email">Email</label>
                    <input
                        id="email"
                        type="email"
                        value={email}
                        onChange={(e) => setEmail(e.target.value)}
                    />
                </div>
                <button className="save-button" onClick={handleSaveProfile}>
                    Save Profile
                </button>
            </section>

            {/* Security Section */}
            <section className="settings-section">
                <h3>Security</h3>
                <div className="form-group">
                    <label htmlFor="password">Change Password</label>
                    <input
                        id="password"
                        type="password"
                        placeholder="Enter new password"
                        value={password}
                        onChange={(e) => setPassword(e.target.value)}
                    />
                </div>
                <div className="form-group checkbox-group">
                    <input
                        id="two-factor"
                        type="checkbox"
                        checked={twoFactor}
                        onChange={(e) => setTwoFactor(e.target.checked)}
                    />
                    <label htmlFor="two-factor">Enable Two-Factor Authentication</label>
                </div>
                <button className="save-button" onClick={handleSaveSecurity}>
                    Save Security Settings
                </button>
            </section>

            {/* Notifications Section */}
            <section className="settings-section">
                <h3>Notifications</h3>
                <div className="form-group checkbox-group">
                    <input
                        id="email-notifications"
                        type="checkbox"
                        checked={emailNotifications}
                        onChange={(e) => setEmailNotifications(e.target.checked)}
                    />
                    <label htmlFor="email-notifications">Email Notifications</label>
                </div>
                <div className="form-group checkbox-group">
                    <input
                        id="sms-notifications"
                        type="checkbox"
                        checked={smsNotifications}
                        onChange={(e) => setSmsNotifications(e.target.checked)}
                    />
                    <label htmlFor="sms-notifications">SMS Notifications</label>
                </div>
                <div className="form-group checkbox-group">
                    <input
                        id="push-notifications"
                        type="checkbox"
                        checked={pushNotifications}
                        onChange={(e) => setPushNotifications(e.target.checked)}
                    />
                    <label htmlFor="push-notifications">Push Notifications</label>
                </div>
                <button className="save-button" onClick={handleSaveNotifications}>
                    Save Notification Preferences
                </button>
            </section>

            {/* Preferences Section */}
            <section className="settings-section">
                <h3>Preferences</h3>
                <div className="form-group">
                    <label htmlFor="theme">Theme</label>
                    <select
                        id="theme"
                        value={theme}
                        onChange={(e) => setTheme(e.target.value)}
                    >
                        <option value="light">Light</option>
                        <option value="dark">Dark</option>
                    </select>
                </div>
                <button className="save-button" onClick={handleSavePreferences}>
                    Save Preferences
                </button>
            </section>
        </div>
    );
};

export default Settings;
