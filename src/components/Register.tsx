import React, {
  useRef,
  useState,
  useEffect,
  ChangeEvent,
  FormEvent,
} from "react";
import { Link, useNavigate } from "react-router-dom";
import { axiosPublic } from "../api/axios";
import {
  Button,
  TextField,

  Typography,
  Box,
  Alert,

  Card,
  CardContent,
} from "@mui/material";

// Validation patterns
const USER_REGEX = /^[A-z][A-z0-9-_]{3,23}$/;
const PWD_REGEX = /^(?=.*[a-z])(?=.*[A-Z])(?=.*[0-9])(?=.*[!@#$%]).{8,24}$/;
const EMAIL_REGEX = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
const ZIP_REGEX = /^[0-9]{5}(?:-[0-9]{4})?$/;
const PHONE_REGEX = /^\d{10,15}$/;

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
  useRef<HTMLDivElement>(null);
  const navigate = useNavigate();

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

  const [, setValidation] = useState<Validation>({
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

  const handleInputChange = (
      e: ChangeEvent<HTMLInputElement | HTMLTextAreaElement | { name: string; value: unknown }>
  ) => {
    const { name, value } = e.target;
    setFormData((prev) => ({ ...prev, [name]: value as string }));
  };

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
    } catch (error: never) {
      setErrMsg(error?.response?.data?.message || "Registration failed.");
    } finally {
      setIsLoading(false);
    }
  };

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
                {errMsg && <Alert severity="error">{errMsg}</Alert>}
                {Object.keys(formData).map((key) => (
                    <TextField
                        key={key}
                        fullWidth
                        margin="normal"
                        name={key}
                        label={key}
                        onChange={handleInputChange}
                        required
                    />
                ))}
                <Button type="submit" disabled={isLoading}>
                  {isLoading ? "Loading" : "Submit"}
                </Button>
              </form>
            </Box>
          </CardContent>
        </Card>
      </section>
  );
};

export default Register;
