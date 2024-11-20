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
    const userRef = useRef();
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
        userRef.current.focus();
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
                }),
                { headers: { "Content-Type": "application/json" ,
                        "Accept": "application/json",

                } }
            );

            if (response.status === 200 || response.status === 201) {
                setSuccess(true);
                navigate("/", { replace: true });
            }
        } catch (error) {
            if (error.response) {
                // Server error
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
                    <form onSubmit={handleSubmit}>
                        {/* Username */}
                        <input
                            type="text"
                            placeholder="Enter your username"
                            ref={userRef}
                            onChange={(e) => setUser(e.target.value)}
                            value={user}
                            required
                        />
                        <FontAwesomeIcon icon={faCheck} className={validName ? "valid" : "hide"} />
                        <FontAwesomeIcon icon={faTimes} className={validName || !user ? "hide" : "invalid"} />

                        {/* Email */}
                        <input
                            type="email"
                            placeholder="Enter email address"
                            onChange={(e) => setEmail(e.target.value)}
                            value={email}
                            required
                        />
                        <FontAwesomeIcon icon={faCheck} className={validEmail ? "valid" : "hide"} />
                        <FontAwesomeIcon icon={faTimes} className={validEmail || !email ? "hide" : "invalid"} />

                        {/* Password */}
                        <input
                            type="password"
                            placeholder="Password"
                            onChange={(e) => setPassword(e.target.value)}
                            value={password}
                            required
                        />
                        <FontAwesomeIcon icon={faCheck} className={validPwd ? "valid" : "hide"} />
                        <FontAwesomeIcon icon={faTimes} className={validPwd || !password ? "hide" : "invalid"} />

                        {/* Confirm Password */}
                        <input
                            type="password"
                            placeholder="Confirm Password"
                            onChange={(e) => setMatchPwd(e.target.value)}
                            value={matchPwd}
                            required
                        />
                        <FontAwesomeIcon icon={faCheck} className={validMatch ? "valid" : "hide"} />
                        <FontAwesomeIcon icon={faTimes} className={validMatch || !matchPwd ? "hide" : "invalid"} />

                        {/* Other Fields */}
                        <input type="date" placeholder="Birthdate" onChange={(e) => setBirthdate(e.target.value)} value={birthdate} required />
                        <input type="text" placeholder="Phone Number" onChange={(e) => setPhoneNumber(e.target.value)} value={phoneNumber} />
                        <FontAwesomeIcon icon={faCheck} className={validPhone ? "valid" : "hide"} />
                        <FontAwesomeIcon icon={faTimes} className={validPhone || !phoneNumber ? "hide" : "invalid"} />

                        <input type="text" placeholder="Address" onChange={(e) => setAddress(e.target.value)} value={address} />
                        <input type="text" placeholder="City" onChange={(e) => setCity(e.target.value)} value={city} />
                        <input type="text" placeholder="State" onChange={(e) => setState(e.target.value)} value={state} />
                        <input type="text" placeholder="Zip Code" onChange={(e) => setZipCode(e.target.value)} value={zipCode} />
                        <input type="text" placeholder="Country" onChange={(e) => setCountry(e.target.value)} value={country} />
                        <select onChange={(e) => setGender(e.target.value)} value={gender}>
                            {genderOptions.map((option, index) => (
                                <option key={index} value={option}>
                                    {option}
                                </option>
                            ))}
                        </select>
                        <textarea placeholder="Bio" onChange={(e) => setBio(e.target.value)} value={bio}></textarea>
                        <select onChange={(e) => setSecurityQuestion(e.target.value)} value={securityQuestion}>
                            {securityQuestions.map((question, index) => (
                                <option key={index} value={question}>
                                    {question}
                                </option>
                            ))}
                        </select>
                        <input
                            type="text"
                            placeholder="Answer"
                            onChange={(e) => setSecurityAnswer(e.target.value)}
                            value={securityAnswer}
                            required
                        />

                        {/* Submit */}
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
