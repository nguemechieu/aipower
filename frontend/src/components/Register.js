import React, { useRef, useState, useEffect } from "react";
import { Link, useNavigate } from "react-router-dom";
import axios from "../api/axios.js";
import {
    Button,
    TextField,
    MenuItem,
    Select,
    InputLabel,
    FormControl,
    CircularProgress,
    Typography,
    Box,
    Alert,
} from "@mui/material";
import "./Register.css";

// Validation patterns
const USER_REGEX = /^[A-z][A-z0-9-_]{3,23}$/;
const PWD_REGEX = /^(?=.*[a-z])(?=.*[A-Z])(?=.*[0-9])(?=.*[!@#$%]).{8,24}$/;
const EMAIL_REGEX = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
const ZIP_REGEX = /^[0-9]{5}(?:-[0-9]{4})?$/;
const PHONE_REGEX = /^\d{10,15}$/;
const genderOptions = ["Male", "Female", "Others"];
const securityQuestions = [
    "What was the name of your first pet?",
    "What is your mother's maiden name?",
    "What is your favorite book?",
    "What was the make and model of your first car?",
    "What is the name of the street you grew up on?",
    "In what city were you born?",
];

const Register = () => {
    useRef(null);
    const errRef = useRef();
    const navigate = useNavigate();

    // State variables
    const [formData, setFormData] = useState({
        username: "",
        email: "",
        password: "",
        matchPwd: "",
        firstName: "",
        middleName: "",
        lastName: "",
        birthdate: "",
        phoneNumber: "",
        zipCode: "",
        address: "",
        city: "",
        state: "",
        country: "",
        gender: genderOptions[0],
        profilePictureUrl: "",
        bio: "",
        securityQuestion: securityQuestions[0],
        securityAnswer: "",
    });

    const [validation, setValidation] = useState({
        validName: false,
        validEmail: false,
        validPwd: false,
        validMatch: false,
        validPhone: false,
        validZip: false,
    });

    const [errMsg, setErrMsg] = useState("");
    const [success, setSuccess] = useState(false);
    const [isLoading, setIsLoading] = useState(false);

    // Validation hooks
    useEffect(() => {
        setValidation({
            validName: USER_REGEX.test(formData.username),
            validEmail: EMAIL_REGEX.test(formData.email),
            validPwd: PWD_REGEX.test(formData.password),
            validMatch: formData.password === formData.matchPwd,
            validPhone: PHONE_REGEX.test(formData.phoneNumber),
            validZip: ZIP_REGEX.test(formData.zipCode),
        });
    }, [formData]);

    useEffect(() => {
        setErrMsg("");
    }, [formData]);

    // Input change handler
    const handleInputChange = (e) => {
        const { name, value } = e.target;
        setFormData((prev) => ({ ...prev, [name]: value }));
    };

    // Form submission
    const handleSubmit = async (e) => {
        e.preventDefault();
        setIsLoading(true);
        setErrMsg("");

        try {
            const response = await axios.post("/api/v3/auth/register", JSON.stringify(formData));

            if (response.status === 200 || response.status === 201) {
                setSuccess(true);
                navigate("/", { replace: true });
            } else {
                setErrMsg("Registration failed!");
            }
        } catch (error) {
            setErrMsg(JSON.stringify(error?.response?.data) || "Registration failed!");
        } finally {
            setIsLoading(false);
        }
    };

    return (
        <section className="register" >
            {success ? (
                <Box textAlign="center">
                    <Typography variant="h4" gutterBottom>
                        Registration Successful!
                    </Typography>
                    <Link to="/" style={{ textDecoration: "none" }}>
                        <Button variant="contained" color="primary">
                            Sign In
                        </Button>
                    </Link>
                </Box>
            ) : (
                <form onSubmit={handleSubmit}>
                    <Typography variant="h4" gutterBottom>
                        Register
                    </Typography>

                    {errMsg && (
                        <Alert severity="error" ref={errRef}>
                            {errMsg}
                        </Alert>
                    )}

                    {/* Dynamic Input Fields */}
                    {[
                        { label: "Username", name: "username", errorKey: "validName", type: "text" },
                        { label: "Email", name: "email", errorKey: "validEmail", type: "email" },
                        { label: "Password", name: "password", errorKey: "validPwd", type: "password" },
                        { label: "Confirm Password", name: "matchPwd", errorKey: "validMatch", type: "password" },
                        { label: "First Name", name: "firstName", type: "text" },
                        { label: "Middle Name", name: "middleName", type: "text" },
                        { label: "Last Name", name: "lastName", type: "text" },
                        { label: "Phone Number", name: "phoneNumber", errorKey: "validPhone", type: "tel" },
                        { label: "Zip Code", name: "zipCode", errorKey: "validZip", type: "text" },
                        { label: "Address", name: "address", type: "text" },
                        { label: "City", name: "city", type: "text" },
                        { label: "State", name: "state", type: "text" },
                        { label: "Country", name: "country", type: "text" },
                        { label: "Profile Picture URL", name: "profilePictureUrl", type: "text" },
                    ].map(({ label, name, type, errorKey }) => (
                        <TextField
                            key={name}
                            fullWidth
                            margin="normal"
                            label={label}
                            name={name}
                            type={type}
                            value={formData[name]}
                            onChange={handleInputChange}
                            error={errorKey && !validation[errorKey]}
                            helperText={errorKey && !validation[errorKey] ? `Invalid ${label.toLowerCase()}` : ""}
                            required={!["middleName", "address", "city", "state", "country", "profilePictureUrl"].includes(name)}
                        />
                    ))}

                    <TextField
                        fullWidth
                        margin="normal"
                        label="Birthdate"
                        name="birthdate"
                        type="date"
                        InputLabel={{ shrink: true }}
                        value={formData.birthdate}
                        onChange={handleInputChange}
                        required
                    />

                    <FormControl fullWidth margin="normal">
                        <InputLabel id="gender-label">Gender</InputLabel>
                        <Select
                            labelId="gender-label"
                            name="gender"
                            value={formData.gender}
                            onChange={handleInputChange}
                            required
                          variant={"filled"}>
                            {genderOptions.map((option) => (
                                <MenuItem key={option} value={option}>
                                    {option}
                                </MenuItem>
                            ))}
                        </Select>
                    </FormControl>

                    <FormControl fullWidth margin="normal">
                        <InputLabel id="security-question-label">Security Question</InputLabel>
                        <Select
                            labelId="security-question-label"
                            name="securityQuestion"
                            value={formData.securityQuestion}
                            onChange={handleInputChange}
                            required
                         variant={"filled"}>
                            {securityQuestions.map((question) => (
                                <MenuItem key={question} value={question}>
                                    {question}
                                </MenuItem>
                            ))}
                        </Select>
                    </FormControl>

                    <TextField
                        fullWidth
                        margin="normal"
                        label="Security Answer"
                        name="securityAnswer"
                        type="text"
                        value={formData.securityAnswer}
                        onChange={handleInputChange}
                        required
                    />

                    <TextField
                        fullWidth
                        margin="normal"
                        label="Bio"
                        name="bio"
                        type="text"
                        value={formData.bio}
                        onChange={handleInputChange}
                        multiline
                        rows={4}
                    />

                    <Button
                        type="submit"
                        variant="contained"
                        color="primary"
                        fullWidth
                        disabled={
                            !validation.validName ||
                            !validation.validEmail ||
                            !validation.validPwd ||
                            !validation.validMatch ||
                            isLoading
                        }
                        sx={{ marginTop: 2 }}
                    >
                        {isLoading ? <CircularProgress size={24} /> : "Register"}
                    </Button>

                    <Typography variant="body2" align="center" sx={{ marginTop: 2 }}>
                        Already registered? <Link to="/">Sign In</Link>
                    </Typography>
                </form>
            )}
        </section>
    );
};

export default Register;
