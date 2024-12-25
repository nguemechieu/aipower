import React, { useState } from "react";
import {
  Box,
  Paper,
  Typography,
  Avatar,
  Button,
  TextField,
  Divider, Grid2,
} from "@mui/material";
import EditIcon from "@mui/icons-material/Edit";
import SaveIcon from "@mui/icons-material/Save";

const Profile = () => {
  const [isEditing, setIsEditing] = useState(false);
  const [user, setUser] = useState({
    name: "John Doe",
    email: "johndoe@example.com",
    phone: "+1234567890",
    address: "1234 Elm Street, Springfield, IL",
    bio: "Software engineer with a passion for building amazing apps.",
    profilePicture: "https://via.placeholder.com/150", // Placeholder profile image
  });

  const handleEditToggle = () => {
    setIsEditing((prev) => !prev);
  };

  const handleInputChange = (e: { target: { name: any; value: any; }; }) => {
    const { name, value } = e.target;
    setUser((prevUser) => ({ ...prevUser, [name]: value }));
  };

  const handleSave = () => {
    setIsEditing(false);
    alert("Profile updated successfully!");
  };

  return (
    <Box sx={{ padding: 4 }}>
      <Typography variant="h4" gutterBottom>
        User Profile
      </Typography>
      <Paper sx={{ padding: 4 }} elevation={3}>
        {/* Profile Picture */}
        <Box
          sx={{
            display: "flex",
            alignItems: "center",
            marginBottom: 4,
          }}
        >
          <Avatar
            src={user.profilePicture}
            alt={user.name}
            sx={{ width: 100, height: 100, marginRight: 2 }}
          />
          <Box>
            <Typography variant="h5">{user.name}</Typography>
            <Typography variant="body1" color="text.secondary">
              {user.email}
            </Typography>
          </Box>
        </Box>

        <Divider sx={{ marginBottom: 3 }} />

        {/* User Details */}
        <Grid2 container spacing={3}>
          <Grid2 item xs={12} sm={6}>
            <TextField
              label="Full Name"
              fullWidth
              value={user.name}
              name="name"
              onChange={handleInputChange}
              disabled={!isEditing}
            />
          </Grid2>
          <Grid2 item xs={12} sm={6}>
            <TextField
              label="Email"
              fullWidth
              value={user.email}
              name="email"
              onChange={handleInputChange}
              disabled={!isEditing}
            />
          </Grid2>
          <Grid2 item xs={12} sm={6}>
            <TextField
              label="Phone"
              fullWidth
              value={user.phone}
              name="phone"
              onChange={handleInputChange}
              disabled={!isEditing}
            />
          </Grid2>
          <Grid2 item xs={12} sm={6}>
            <TextField
              label="Address"
              fullWidth
              value={user.address}
              name="address"
              onChange={handleInputChange}
              disabled={!isEditing}
            />
          </Grid2>
          <Grid2 item xs={12}>
            <TextField
              label="Bio"
              fullWidth
              multiline
              rows={4}
              value={user.bio}
              name="bio"
              onChange={handleInputChange}
              disabled={!isEditing}
            />
          </Grid2>
        </Grid2>

        {/* Action Buttons */}
        <Box sx={{ marginTop: 3, textAlign: "right" }}>
          {isEditing ? (
            <Button
              variant="contained"
              color="primary"
              startIcon={<SaveIcon />}
              onClick={handleSave}
            >
              Save
            </Button>
          ) : (
            <Button
              variant="outlined"
              color="secondary"
              startIcon={<EditIcon />}
              onClick={handleEditToggle}
            >
              Edit Profile
            </Button>
          )}
        </Box>
      </Paper>
    </Box>
  );
};

export default Profile;
