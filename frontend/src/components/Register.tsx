import React, { useRef, useState, useEffect, ChangeEvent, FormEvent } from "react";
import { Link, useNavigate } from "react-router-dom";
import { axiosPublic } from "../api/axios";
import {
  Button,
  TextField,
  Select,
  FormControl,
  CircularProgress,
  Typography,
  Box,
  Alert,
  InputLabel,
  MenuItem, Card, CardContent,
} from "@mui/material";


// Validation patterns
const USER_REGEX = /^[A-z][A-z0-9-_]{3,23}$/;
const PWD_REGEX = /^(?=.*[a-z])(?=.*[A-Z])(?=.*[0-9])(?=.*[!@#$%]).{8,24}$/;
const EMAIL_REGEX = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
const ZIP_REGEX = /^[0-9]{5}(?:-[0-9]{4})?$/;
const PHONE_REGEX = /^\d{10,15}$/;

// Constants
const genderOptions = ["Male", "Female", "Others"] as const;
const securityQuestions = [
  "What was the name of your first pet?",
  "What is your mother's maiden name?",
  "What is your favorite book?",
  "What was the make and model of your first car?",
  "What is the name of the street you grew up on?",
  "In what city were you born?",
];

type Gender = (typeof genderOptions)[number];
type SecurityQuestion = (typeof securityQuestions)[number];

interface FormData {
  username: string;
  email: string;
  password: string;
  matchPwd: string;
  firstName: string;
  middleName?: string;
  lastName: string;
  birthdate: string;
  phoneNumber: string;
  zipCode: string;
  address?: string;
  city?: string;
  state?: string;
  country?: string;
  gender: Gender;
  profilePictureUrl?: string;
  bio?: string;
  securityQuestion: SecurityQuestion;
  securityAnswer: string;
}

interface Validation {
  validName: boolean;
  validEmail: boolean;
  validPwd: boolean;
  validMatch: boolean;
  validPhone: boolean;
  validZip: boolean;
}

const Register: React.FC = () => {
  const errRef = useRef<HTMLDivElement>(null);
  const navigate = useNavigate();

  // State variables
  const [formData, setFormData] = useState<FormData>({
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

  const [validation, setValidation] = useState<Validation>({
    validName: false,
    validEmail: false,
    validPwd: false,
    validMatch: false,
    validPhone: false,
    validZip: false,
  });

  const [errMsg, setErrMsg] = useState<string>("");
  const [success, setSuccess] = useState<boolean>(false);
  const [isLoading, setIsLoading] = useState<boolean>(false);

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
  const handleInputChange = (
      e: ChangeEvent<HTMLInputElement | HTMLTextAreaElement | { name: string; value: unknown }>
  ) => {
    const { name, value } = e.target;
    setFormData((prev) => ({ ...prev, [name]: value as string }));
  };

  // Form submission
  const handleSubmit = async (e: FormEvent) => {
    e.preventDefault();
    setIsLoading(true);
    setErrMsg("");

    try {
      const response = await axiosPublic.post("/api/v3/register", JSON.stringify(formData));
      if ([200, 201].includes(response.status)) {
        setSuccess(true);
        navigate("/", { replace: true });
      } else {
        setErrMsg("Registration failed.");
      }
    } catch (error){
      setErrMsg(
          JSON.stringify({error}));
    } finally {
      setIsLoading(false);
    }
  };

  const inputFields = [
    { label: "Username", name: "username", type: "text", errorKey: "validName" },
    { label: "Email", name: "email", type: "email", errorKey: "validEmail" },
    { label: "Password", name: "password", type: "password", errorKey: "validPwd" },
    { label: "Confirm Password", name: "matchPwd", type: "password", errorKey: "validMatch" },
    { label: "First Name", name: "firstName", type: "text" },
    { label: "Middle Name", name: "middleName", type: "text" },
    { label: "Last Name", name: "lastName", type: "text" },
    { label: "Phone Number", name: "phoneNumber", type: "tel", errorKey: "validPhone" },
    { label: "Zip Code", name: "zipCode", type: "text", errorKey: "validZip" },
    { label: "Address", name: "address", type: "text" },
    { label: "City", name: "city", type: "text" },
    { label: "State", name: "state", type: "text" },
    { label: "Country", name: "country", type: "text" },
    { label: "Profile Picture URL", name: "profilePictureUrl", type: "text" },
  ];

  return success ? (
      <section>
        <Box textAlign="center">
          <Typography variant="h4">Registration Successful!</Typography>
          <Link to="/" style={{ textDecoration: "none" }}>
            <Button variant="contained" color="primary">
              Sign In
            </Button>
          </Link>
        </Box>
      </section>
  ) : (
      <section>
        <Card>
            <CardContent>
          <Box textAlign={"center"}>
        <form onSubmit={handleSubmit}>
          <Typography variant="h4" gutterBottom>
            Register
          </Typography>
          {errMsg && (
              <Alert severity="error" ref={errRef}>
                {errMsg}
              </Alert>
          )}
          {inputFields.map(({ label, name, type, errorKey }) => (
              <TextField
                  key={name}
                  fullWidth
                  margin="normal"
                  label={label}
                  name={name}
                  type={type}
                  value={formData[name as keyof FormData]}
                  onChange={handleInputChange}
                  error={errorKey ? !validation[errorKey as keyof Validation] : false}
                  helperText={
                      errorKey &&
                      !validation[errorKey as keyof Validation] &&
                      `Invalid ${label.toLowerCase()}`
                  }
                  required={
                    !["middleName", "address", "city", "state", "country", "profilePictureUrl"].includes(
                        name
                    )
                  }
              />
          ))}
          <TextField
              fullWidth
              margin="normal"
              label="Birthdate"
              name="birthdate"
              type="date"
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
              value={formData.securityAnswer}
              onChange={handleInputChange}
              type="text"
              required
          />
          <TextField
              fullWidth
              margin="normal"
              label="Bio"
              name="bio"
              multiline
              rows={4}
              value={formData.bio}
              onChange={handleInputChange}
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
        </form> </Box></CardContent>

                  </Card>
      </section>
  );
};

export default Register;
