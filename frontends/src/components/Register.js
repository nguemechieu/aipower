import React, { useRef, useState, useEffect } from "react";
import { Link, useNavigate } from "react-router-dom";
import axios from "../api/axios.js";
import "./Register.css";
import useAuth from "../hooks/useAuth";
import {Button} from "@mui/material"; // Add custom CSS for better styling

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
    const userRef = useRef(null);
    const errRef = useRef();
    const navigate = useNavigate();

    // State variables
    const [username, setUsername] = useState("");
    const [validName, setValidName] = useState(false);
    const [email, setEmail] = useState("");
    const [validEmail, setValidEmail] = useState(false);
    const [password, setPassword] = useState("");
    const [validPwd, setValidPwd] = useState(false);
    const [matchPwd, setMatchPwd] = useState("");
    const [validMatch, setValidMatch] = useState(false);
    const [firstName, setFirstName] = useState("");
    const [lastName, setLastName] = useState("");
    const [middleName, setMiddleName] = useState("");
    const [birthdate, setBirthdate] = useState("");
    const [phoneNumber, setPhoneNumber] = useState("");
    const [validPhone, setValidPhone] = useState(false);
    const [zipCode, setZipCode] = useState("");
    const [validZip, setValidZip] = useState(false);
    const [address, setAddress] = useState("");
    const [city, setCity] = useState("");
    const [state, setState] = useState("");
    const [country, setCountry] = useState("");
    const [gender, setGender] = useState(genderOptions[0]);
    const [profilePictureUrl, setProfilePictureUrl] = useState("");
    const [bio, setBio] = useState("");
    const [securityQuestion, setSecurityQuestion] = useState(securityQuestions[0]);
    const [securityAnswer, setSecurityAnswer] = useState("");
    const [errMsg, setErrMsg] = useState("");
    const [success, setSuccess] = useState(false);
    const [isLoading, setIsLoading] = useState(false);
    const setAuth=useAuth()
    useEffect(() => {
        userRef.current?.focus();
    }, []);

    useEffect(() => {
        setValidName(USER_REGEX.test(username));
        setValidEmail(EMAIL_REGEX.test(email));
        setValidPwd(PWD_REGEX.test(password));
        setValidMatch(password === matchPwd);
        setValidPhone(PHONE_REGEX.test(phoneNumber));
        setValidZip(ZIP_REGEX.test(zipCode));

    }, [username, email, password, matchPwd, phoneNumber, zipCode]);

    useEffect(() => {
        setErrMsg("");
    }, [username, email, password, matchPwd]);

    const handleSubmit = async (e) => {
        e.preventDefault();
        setIsLoading(true);
        setErrMsg("");

        try {
            const response = await axios.post(
                "/api/v3/auth/register",
            {
                    username,
                    firstName,
                    lastName,
                    middleName,
                    email,
                    password,
                    birthdate,
                    phoneNumber,
                    address,
                    city,
                    state,
                    zipCode,
                    country,
                    gender,
                    profilePictureUrl,
                    bio,
                    securityQuestion,
                    securityAnswer
                },

                {
                    headers: { "Content-Type": "application/json" },
                }
            );
            console.log(response.data);
            if (response.status === 200 || response.status === 201) {

                setAuth({
                    id: response.data.id,
                    role: response.data.role,
                    rememberMe: true,
                    accessToken: response.data.accessToken,
                    refreshToken: response.data.refreshToken}
                )
                setSuccess(true);
                navigate("/", { replace: true });
            }else{
                setSuccess(false);
                setErrMsg(response.data|| "Registration failed!");
                errRef.current?.focus();
                setTimeout(
                    () => {
                        setSuccess(false);
                        setErrMsg("");
                    },
                    10000
                )
            }
        } catch (error) {
            setSuccess(false);
            setErrMsg(error.message || "Registration failed!");

            errRef.current?.focus();
            setTimeout(
                () => {
                    setSuccess(false);
                    setErrMsg("");
                    navigate("/", { replace: true });
                },
                10000
            )
        } finally {
            setIsLoading(false);
        }
    };

    return (<div className="flex-grow">
        <section className="register">
            {success ? (
                <div>
                    <h1>Registration Successful!</h1>
                    <p>
                        <Link to="/">Sign In</Link>
                    </p>
                </div>
            ) : (
                <div>
                    <h1>Register</h1>
                    <div className={'instructions'}>
                        <p>
                            {(validName) ?  "Please Enter a valid name" : ""}

                            {(validEmail) ? "Please Enter a valid email" : ""}

                            {(validPwd) ? "Please Enter a valid password" : ""}


                            {(validPhone) ? "Please Enter a valid phone number" : ""}

                        </p>
                    </div>
                    <form onSubmit={handleSubmit}>
                        <input
                            id="username"
                            type="text"
                            placeholder="Username"
                            ref={userRef}
                            value={username}
                            onChange={(e) => setUsername(e.target.value)}
                            required
                            aria-invalid={!validName}
                        />
                        <input
                            id="firstName"
                            type="text"
                            placeholder="First Name"
                            value={firstName}
                            onChange={(e) => setFirstName(e.target.value)}
                            required
                        />

                        <input
                            id="middleName"
                            type="text"
                            placeholder="Middle Name"
                            value={middleName}
                            onChange={(e) => setMiddleName(e.target.value)}
                        />
                        <input
                            id="lastName"
                            type="text"
                            placeholder="Last Name"
                            value={lastName}
                            onChange={(e) => setLastName(e.target.value)}
                            required
                        />
                        <input
                            id="email"
                            type="email"
                            placeholder="Email"
                            value={email}
                            onChange={(e) => setEmail(e.target.value)}
                            required
                            aria-invalid={!validEmail}
                        />
                        <input
                            id="password"
                            type="password"
                            placeholder="Password"
                            value={password}
                            onChange={(e) => setPassword(e.target.value)}
                            required
                            aria-invalid={!validPwd}
                        />
                        <input
                            id="confirm-password"
                            type="password"
                            placeholder="Confirm Password"
                            value={matchPwd}
                            onChange={(e) => setMatchPwd(e.target.value)}
                            required
                            aria-invalid={!validMatch}
                        />
                        <input
                            id="birthdate"
                            type="date"
                            value={birthdate}
                            onChange={(e) => setBirthdate(e.target.value)}
                            required
                        />
                        <input
                            id="phoneNumber"
                            type="tel"
                            placeholder="Phone Number"
                            value={phoneNumber}
                            onChange={(e) => setPhoneNumber(e.target.value)}
                            aria-invalid={!validPhone}
                        />
                        <input
                            id="zipCode"
                            type="text"
                            placeholder="Zip Code"
                            value={zipCode}
                            onChange={(e) => setZipCode(e.target.value)}
                            aria-invalid={!validZip}
                        />
                        <input
                            id="address"
                            type="text"
                            placeholder="Address"
                            value={address}
                            onChange={(e) => setAddress(e.target.value)}
                        />
                        <input
                            id="city"
                            type="text"
                            placeholder="City"
                            value={city}
                            onChange={(e) => setCity(e.target.value)}
                        />
                        <input
                            id="state"
                            type="text"
                            placeholder="State"
                            value={state}
                            onChange={(e) => setState(e.target.value)}
                        />
                        <input
                            id="country"
                            type="text"
                            placeholder="Country"
                            value={country}
                            onChange={(e) => setCountry(e.target.value)}
                        />
                        <select
                            id="gender"
                            value={gender}
                            onChange={(e) => setGender(e.target.value)}
                            required
                        >
                            {genderOptions.map((option, index) => (
                                <option key={index} value={option}>
                                    {option}
                                </option>
                            ))}
                        </select>
                        <input
                            id="profilePictureUrl"
                            type="text"
                            placeholder="Profile Picture URL"
                            value={profilePictureUrl}
                            onChange={(e) => setProfilePictureUrl(e.target.value)}
                        />
                        <textarea
                            id="bio"
                            placeholder="Bio"
                            value={bio}
                            onChange={(e) => setBio(e.target.value)}
                        ></textarea>
                        <select
                            id="securityQuestion"
                            value={securityQuestion}
                            onChange={(e) => setSecurityQuestion(e.target.value)}
                        >
                            {securityQuestions.map((question, index) => (
                                <option key={index} value={question}>
                                    {question}
                                </option>
                            ))}
                        </select>
                        <input
                            id="securityAnswer"
                            type="text"
                            placeholder="Answer to Security Question"
                            value={securityAnswer}
                            onChange={(e) => setSecurityAnswer(e.target.value)}
                            required
                        />

                        <Button
                            type="submit"
                            disabled={!validName || !validEmail || !validPwd || !validMatch || isLoading}
                        >

                            {isLoading ? "Registering..." : "Register"}
                        </Button>
                    </form>
                    <p ref={errRef} className={errMsg ? "errmsg" : "offscreen"} aria-live="assertive">
                        {errMsg}
                    </p>
                    <p>
                        Already registered? <Link to="/">Sign In</Link>
                    </p>
                </div>
            )}
        </section></div>
    );
};

export default Register;
