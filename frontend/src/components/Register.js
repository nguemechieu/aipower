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

  // State variables for each form field and validation
  const [user, setUser] = useState("");
  const [validName, setValidName] = useState(false);

  const [email, setEmail] = useState("");
  const [validEmail, setValidEmail] = useState(false);

  const [firstName, setFirstName] = useState("");
  const [lastName, setLastName] = useState("");
  const [middleName, setMiddleName] = useState("");
  const [role, setRole] = useState("USER");

  const [dob, setDOB] = useState("");
  const [validDOB, setValidDOB] = useState(false);

  const [phone, setPhone] = useState("");
  const [validPhone, setValidPhone] = useState(false);

  const [address, setAddress] = useState("");
  const [city, setCity] = useState("");
  const [state, setState] = useState("");
  const [zipCode, setZipCode] = useState("");
  const [validZip, setValidZip] = useState(false);
  const [country, setCountry] = useState("");

  const [pwd, setPwd] = useState("");
  const [validPwd, setValidPwd] = useState(false);

  const [matchPwd, setMatchPwd] = useState("");
  const [validMatch, setValidMatch] = useState(false);

  const [gender, setGender] = useState("");
  const [profilePictureUrl, setProfilePictureUrl] = useState("");
  const [bio, setBio] = useState("");
  const [securityQuestion, setSecurityQuestion] = useState(securityQuestions[0]);
  const [securityAnswer, setSecurityAnswer] = useState("");
  const [twoFactorEnabled, setTwoFactorEnabled] = useState(false);

  const [friendCount, setFriendCount] = useState(0);
  const [postCount, setPostCount] = useState(0);
  const [followerCount, setFollowerCount] = useState(0);
  const [followingCount, setFollowingCount] = useState(0);

  const [errMsg, setErrMsg] = useState("");
  const [success, setSuccess] = useState(false);
  const [isLoading, setIsLoading] = useState(false);

  // Focus the username input on load
  useEffect(() => {
    userRef.current.focus();
  }, []);

  // Validate fields on input change
  useEffect(() => setValidName(USER_REGEX.test(user)), [user]);
  useEffect(() => setValidEmail(EMAIL_REGEX.test(email)), [email]);
  useEffect(() => setValidDOB(new Date(dob) < new Date()), [dob]);
  useEffect(() => setValidZip(ZIP_REGEX.test(zipCode)), [zipCode]);
  useEffect(() => setValidPhone(PHONE_REGEX.test(phone)), [phone]);
  useEffect(() => {
    setValidPwd(PWD_REGEX.test(pwd));
    setValidMatch(pwd === matchPwd);
  }, [pwd, matchPwd]);

  useEffect(() => setErrMsg(""), [user, email, pwd, matchPwd, dob, zipCode, phone]);

  const handleSubmit = async (e) => {
    e.preventDefault();
    setIsLoading(true);
    setErrMsg("");

    try {
      // Clear cookies/local storage if necessary
      localStorage.clear();
      sessionStorage.clear();
      const response = await axios.post(
          REGISTER_URL, {
            username: user,
            password: pwd,
            email,
            firstName,
            lastName,
            middleName,
            birthdate: dob,
            phoneNumber: phone,
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
            friendCount,
            postCount,
            followerCount,
            followingCount,
            profilePictureUrl
          }
      );

      if (response.status === 200) {
        setSuccess(true);
        // Reset all form fields after success
        setUser("");
        setPwd("");
        setEmail("");
        setFirstName("");
        setLastName("");
        setMiddleName("");
        setDOB("");
        setPhone("");
        setAddress("");
        setCity("");
        setState("");
        setZipCode("");
        setCountry("");
        setGender("");
        setProfilePictureUrl("");
        setBio("");
        setSecurityQuestion(securityQuestions[0]);
        setSecurityAnswer("");
        setTwoFactorEnabled(false);
        setFriendCount(0);
        setPostCount(0);
        setFollowerCount(0);
        setFollowingCount(0);

        // Navigate to Sign In page
        navigate("/", { replace: true });
      } else {
        setErrMsg(response.statusText);
      }
    } catch (err) {
      setErrMsg(err.message || "Registration failed");
    } finally {
      setIsLoading(false);
      if (errMsg) errRef.current.focus();
    }
  };

  return (
      <section>
        {success ? (
            <div>
              <h1>Success!</h1>
              <p>
                <Link to="/">Sign In</Link>
              </p>
            </div>
        ) : (
            <div>
              <p ref={errRef} className={errMsg ? "errmsg" : "offscreen"}>{errMsg}</p>
              <h1>Register</h1>
              <form onSubmit={handleSubmit}>
                <input type="hidden" name="roles" value={role} />

                {/* Username */}
                <div>
                  <FontAwesomeIcon icon={faCheck} className={validName ? "valid" : "hide"} />
                  <FontAwesomeIcon icon={faTimes} className={validName || !user ? "hide" : "invalid"} />
                  <input
                      type="text"
                      id="username"
                      placeholder="Enter username"
                      ref={userRef}
                      onChange={(e) => setUser(e.target.value)}
                      value={user}
                      required
                  />
                </div>

                {/* Email */}
                <div>
                  <FontAwesomeIcon icon={faCheck} className={validEmail ? "valid" : "hide"} />
                  <FontAwesomeIcon icon={faTimes} className={validEmail || !email ? "hide" : "invalid"} />
                  <input type="email" id="email" placeholder="Enter email" onChange={(e) => setEmail(e.target.value)} value={email} required />
                </div>

                {/* Additional fields */}
                <div>
                  <input type="text" id="firstName" placeholder="Enter first name" onChange={(e) => setFirstName(e.target.value)} value={firstName} required />
                  <input type="text" id="lastName" placeholder="Enter last name" onChange={(e) => setLastName(e.target.value)} value={lastName} required />
                  <input type="text" id="middleName" placeholder="Enter middle name" onChange={(e) => setMiddleName(e.target.value)} value={middleName} />
                  <input type="text" id="phoneNumber" placeholder="Enter phone number" onChange={(e) => setPhone(e.target.value)} value={phone} required />
                  <input type="date" id="dob" placeholder="Enter date of birth" onChange={(e) => setDOB(e.target.value)} value={dob} required />
                </div>

                <div>
                  <label htmlFor="gender">Gender</label>
                  <select id="gender" onChange={(e) => setGender(e.target.value)} value={gender}>
                    {genderOptions.map((option, index) => (
                        <option key={index} value={option}>
                          {option}
                        </option>
                    ))}
                  </select>
                </div>

                <div>
                  <label htmlFor="securityQuestion">Security Question</label>
                  <select id="securityQuestion" onChange={(e) => setSecurityQuestion(e.target.value)} value={securityQuestion}>
                    {securityQuestions.map((question, index) => (
                        <option key={index} value={question}>
                          {question}
                        </option>
                    ))}
                  </select>
                  <input type="text" id="securityAnswer" placeholder="Security Answer" onChange={(e) => setSecurityAnswer(e.target.value)} value={securityAnswer} />
                </div>

                {/* Two-Factor Enabled */}
                <div>
                  <label>
                    <input type="checkbox" checked={twoFactorEnabled} onChange={(e) => setTwoFactorEnabled(e.target.checked)} />
                    Enable Two-Factor Authentication
                  </label>
                </div>

                <button type="submit" id="btnRegister">
                  {isLoading ? "Registering..." : "Sign Up"}
                </button>
              </form>
              <p>Already registered? <Link to="/">Sign In</Link></p>
            </div>
        )}
      </section>
  );
};

export default Register;
