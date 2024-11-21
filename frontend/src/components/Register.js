import React, { useRef, useState, useEffect } from "react";
import { faCheck, faTimes } from "@fortawesome/free-solid-svg-icons";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { Link, useNavigate } from "react-router-dom";
import axios from "../api/axios.js";
import "./Register.css"; // Add custom CSS for better styling

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
    const userRef = useRef(null); // Ensure userRef is initialized to null
    const errRef = useRef();
    const navigate = useNavigate();

    // State variables
    const [user, setUser] = useState("");
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
    const [gender, setGender] = useState("");
    const [profilePictureUrl, setProfilePictureUrl] = useState("");
    const [bio, setBio] = useState("");
    const [securityQuestion, setSecurityQuestion] = useState(securityQuestions[0]);
    const [securityAnswer, setSecurityAnswer] = useState("");
    const [errMsg, setErrMsg] = useState("");
    const [success, setSuccess] = useState(false);
    const [isLoading, setIsLoading] = useState(false);

    // Focus on username input on mount
    useEffect(() => {
        userRef.current;
    }, []);

    // Validation logic
    useEffect(() => setValidName(USER_REGEX.test(user)), [user]);
    useEffect(() => setValidEmail(EMAIL_REGEX.test(email)), [email]);
    useEffect(() => {
        setValidPwd(PWD_REGEX.test(password));
        setValidMatch(password === matchPwd);
    }, [password, matchPwd]);
    useEffect(() => setValidPhone(PHONE_REGEX.test(phoneNumber)), [phoneNumber]);
    useEffect(() => setValidZip(ZIP_REGEX.test(zipCode)), [zipCode]);
    useEffect(() => setErrMsg(""), [user, email, password, matchPwd]);

    const handleSubmit = async (e) => {
        e.preventDefault();
        setIsLoading(true);
        setErrMsg("");

        try {
            const response = await axios.post(
                "/api/v3/auth/register",
                JSON.stringify({
                    username: user,
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
                    securityAnswer,
                }),
                { headers: { "Content-Type": "application/json", Accept: "application/json" } }
            );

            if (response.status === 200 || response.status === 201) {
                setSuccess(true);
                navigate("/", { replace: true });
            }
        } catch (error) {
            if (error.response) {
                if (error.response.status === 400) {
                    setErrMsg("Invalid data provided. Please check your inputs.");
                } else if (error.response.status === 403) {
                    setErrMsg("Access Denied: Not authorized");
                } else {
                    setErrMsg("An error occurred while processing your request.");
                }
            } else {
                setErrMsg("No server response. Please try again later.");
            }
        } finally {
            setIsLoading(false);
        }
    };

    return (
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
                    <form onSubmit={handleSubmit} >
                        <input id="username" type="text" placeholder="Username" value={user} onChange={(e) => setUser(e.target.value)} required />
                        <input id="first-name" type="text" placeholder="First Name" value={firstName} onChange={(e) => setFirstName(e.target.value)} required />
                        <input id="middle-name" type="text" placeholder="Middle Name" value={middleName} onChange={(e) => setMiddleName(e.target.value)} />
                        <input id="last-name" type="text" placeholder="Last Name" value={lastName} onChange={(e) => setLastName(e.target.value)} required />
                        <input id="email" type="email" placeholder="Email" value={email} onChange={(e) => setEmail(e.target.value)} required />
                        <input id="password" type="password" placeholder="Password" value={password} onChange={(e) => setPassword(e.target.value)} required />
                        <input id="confirm-password" type="password" placeholder="Confirm Password" value={matchPwd} onChange={(e) => setMatchPwd(e.target.value)} required />
                        <input id="birthdate" type="date" value={birthdate} onChange={(e) => setBirthdate(e.target.value)} required />
                        <input id="phone-number" type="tel" placeholder="Phone Number" value={phoneNumber} onChange={(e) => setPhoneNumber(e.target.value)} />
                        <input id="zip-code" type="text" placeholder="Zip Code" value={zipCode} onChange={(e) => setZipCode(e.target.value)} />
                        <input id="address" type="text" placeholder="Address" value={address} onChange={(e) => setAddress(e.target.value)} />
                        <input id="city" type="text" placeholder="City" value={city} onChange={(e) => setCity(e.target.value)} />
                        <input id="state" type="text" placeholder="State" value={state} onChange={(e) => setState(e.target.value)} />
                        <input id="country" type="text" placeholder="Country" value={country} onChange={(e) => setCountry(e.target.value)} />
                        <select id="gender" name={'gender'} value={gender} onChange={(e) => setGender(e.target.value)} required>
                            {genderOptions.map((option, index) => (
                                <option key={index} value={option}>
                                    {  option?"Male":genderOptions[index]}
                                </option>
                            ))}
                        </select>
                        <input id="profile-picture-url" type="text" placeholder="Profile Picture URL" value={profilePictureUrl} onChange={(e) => setProfilePictureUrl(e.target.value)} />
                        <textarea id="bio" placeholder="Bio" value={bio} onChange={(e) => setBio(e.target.value)}></textarea>
                        <select id="security-question" value={securityQuestion} onChange={(e) => setSecurityQuestion(e.target.value)}>
                            {securityQuestions.map((question, index) => (
                                <option key={index} value={question}>
                                    {question}
                                </option>
                            ))}
                        </select>
                        <input id="security-answer" type="text" placeholder="Answer to Security Question" value={securityAnswer} onChange={(e) => setSecurityAnswer(e.target.value)} required />
                        <button type="submit" disabled={!validName || !validEmail || !validPwd || !validMatch || isLoading}>
                            {isLoading ? "Registering..." : "Register"}
                        </button>
                    </form>
                    <p ref={errRef} className={errMsg ? "errmsg" : "offscreen"}>
                        {errMsg}
                    </p>
                    <p>
                        Already registered? <Link to="/">Sign In</Link>
                    </p>
                </div>
            )}
        </section>
    );
};

export default Register;
