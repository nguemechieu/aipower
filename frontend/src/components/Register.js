
import React from 'react';

import { useRef, useState, useEffect } from "react";
import { faCheck, faTimes } from "@fortawesome/free-solid-svg-icons";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { Link, useNavigate } from "react-router-dom";
import axios from "../api/axios.js";

// Validation patterns
const USER_REGEX = /^[A-z][A-z0-9-_]{3,23}$/;
const PWD_REGEX = /^(?=.*[a-z])(?=.*[A-Z])(?=.*[0-9])(?=.*[!@#$%]).{8,24}$/;
const EMAIL_REGEX = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
const ZIP_REGEX = /^[0-9]{5}(?:-[0-9]{4})?$/;
const PHONE_REGEX = /^\d{10,15}$/;

const genderOptions = ["Male", "Female", "Others"];
const REGISTER_URL = "/api/v3/auth/register";
const securityQuestions = [
  "What was the name of your first pet?",
  "What is your mother's maiden name?",
  "What is your favorite book?",
  "What was the make and model of your first car?",
  "What is the name of the street you grew up on?",
  "In what city were you born?"
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
  const [firstName, setFirstName] = useState("");
  const [lastName, setLastName] = useState("");
  const [middleName, setMiddleName] = useState("");
  const [role] = useState("USER");
  const [birthdate, setBirthdate] = useState("");
  const [validDOB, setValidDOB] = useState(false);
  const [phoneNumber, setPhoneNumber] = useState("");
  const [validPhone, setValidPhone] = useState(false);
  const [address, setAddress] = useState("");
  const [city, setCity] = useState("");
  const [state, setState] = useState("");
  const [zipCode, setZipCode] = useState("");
  const [validZip, setValidZip] = useState(false);
  const [country, setCountry] = useState("");
  const [password, setPassword] = useState("");
  const [validPwd, setValidPwd] = useState(false);
  const [matchPwd, setMatchPwd] = useState("");
  const [validMatch, setValidMatch] = useState(false);
  const [gender, setGender] = useState("");
  const [profilePictureUrl, setProfilePictureUrl] = useState("");
  const [bio, setBio] = useState("");
  const [securityQuestion, setSecurityQuestion] = useState(securityQuestions[0]);
  const [securityAnswer, setSecurityAnswer] = useState("");
  const [twoFactorEnabled, setTwoFactorEnabled] = useState(false);
  const [friendCount] = useState(0);
  const [postCount] = useState(0);
  const [followerCount] = useState(0);
  const [followingCount] = useState(0);

  const [errMsg, setErrMsg] = useState("");
  const [success, setSuccess] = useState(false);
  const [isLoading, setIsLoading] = useState(false);

  useEffect(() => {
    userRef.current.focus();
  }, []);

  useEffect(() => setValidName(USER_REGEX.test(user)), [user]);
  useEffect(() => setValidEmail(EMAIL_REGEX.test(email)), [email]);
  useEffect(() => setValidDOB(new Date(birthdate) < new Date()), [birthdate]);
  useEffect(() => setValidZip(ZIP_REGEX.test(zipCode)), [zipCode]);
  useEffect(() => setValidPhone(PHONE_REGEX.test(phoneNumber)), [phoneNumber]);
  useEffect(() => {
    setValidPwd(PWD_REGEX.test(password));
    setValidMatch(password === matchPwd);
  }, [password, matchPwd]);

  useEffect(() => setErrMsg(""), [user, email, password, matchPwd, birthdate, zipCode, phoneNumber]);

  const handleSubmit = async (e) => {
    e.preventDefault();
    setIsLoading(true);
    setErrMsg("");

    try {
      const response = await axios.post(REGISTER_URL, {
        username: user,
        password,
        email,
        firstName,
        lastName,
        middleName,
        birthdate,
        phoneNumber,
        gender,
        bio,
        address,
        city,
        state,
        zipCode,
        country,
        securityQuestion,
        securityAnswer,
        twoFactorEnabled,
        profilePictureUrl
      });

      if (response.status === 200) {
        setSuccess(true);
        navigate("/", { replace: true });
      } else {
        setErrMsg(response.statusText);
      }
    } catch (err) {
      setErrMsg(err.response?.data?.message || "Registration failed");
    } finally {
      setIsLoading(false);
    }
  };

  return (
      <section>
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
                <div>
                  <input
                      type="text"
                      placeholder="Username"
                      ref={userRef}
                      onChange={(e) => setUser(e.target.value)}
                      value={user}
                      required
                  />
                  <FontAwesomeIcon icon={faCheck} className={validName ? "valid" : "hide"}/>
                  <FontAwesomeIcon icon={faTimes} className={validName || !user ? "hide" : "invalid"}/>
                </div>

                {/* Email */}
                <div>
                  <input
                      type="email"
                      placeholder="Email"
                      onChange={(e) => setEmail(e.target.value)}
                      value={email}
                      required
                  />
                  <FontAwesomeIcon icon={faCheck} className={validEmail ? "valid" : "hide"}/>
                  <FontAwesomeIcon icon={faTimes} className={validEmail || !email ? "hide" : "invalid"}/>
                </div>

                {/* Other fields */}
                <input type="text" placeholder="First Name" onChange={(e) => setFirstName(e.target.value)}
                       value={firstName} required/>
                <input type="text" placeholder="Last Name" onChange={(e) => setLastName(e.target.value)}
                       value={lastName} required/>
                <input type="text" placeholder="Middle Name" onChange={(e) => setMiddleName(e.target.value)}
                       value={middleName}/>
                <input type="date" placeholder="Birthdate" onChange={(e) => setBirthdate(e.target.value)}
                       value={birthdate} required/>
                <input type="text" placeholder="Phone Number" onChange={(e) => setPhoneNumber(e.target.value)}
                       value={phoneNumber} required/>
                <input type="text" placeholder="Address" onChange={(e) => setAddress(e.target.value)} value={address}/>
                <input type="text" placeholder="City" onChange={(e) => setCity(e.target.value)} value={city}/>
                <input type="text" placeholder="State" onChange={(e) => setState(e.target.value)} value={state}/>
                <input type="text" placeholder="Zip Code" onChange={(e) => setZipCode(e.target.value)} value={zipCode}/>
                <input type="text" placeholder="Country" onChange={(e) => setCountry(e.target.value)} value={country}/>
                <input type="text" placeholder="Profile Picture URL"
                       onChange={(e) => setProfilePictureUrl(e.target.value)} value={profilePictureUrl}/>
                <textarea placeholder="Bio" onChange={(e) => setBio(e.target.value)} value={bio}></textarea>
                <select onChange={(e) => setGender(e.target.value)} value={gender}>
                  {genderOptions.map((option, index) => (
                      <option key={index} value={option}>
                        {option}
                      </option>
                  ))}
                </select>

                <button type="submit"

                        onClick={
                          (e) => {
                            e.preventDefault();
                            handleSubmit(e).then(r => console.log(r));
                          }
                        }
                        disabled={!validName || !validEmail || !validPwd || !validMatch || isLoading}>
                  {isLoading ? "Registering..." : "Register"}
                </button>
                <p ref={errRef} className={errMsg ? "errmsg" : "offscreen"}>{errMsg}</p>

              </form>
              <p>Already registered? <Link to="/">Sign In</Link></p>
            </div>
        )}
      </section>
  );
};

export default Register;
