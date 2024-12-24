import React, { useEffect, useState } from "react";
import { axiosPublic } from "../api/axios";

const PrivacyPolicy = () => {
  const [effective_date, setEffective_date] = useState("");
  const [terms_of_service, setTerms_of_service] = useState();
  const [data_security_policy, setData_security_policy] = useState();
  const [cookie_policy, setCookie_policy] = useState();

  const [error, setError] = useState(null);

  useEffect(() => {
    setEffective_date(new Date().toString());
    // Fetch the latest privacy policy, terms of service, data security policy, and cookie policy from your server
    // Replace the following placeholders with your actual API endpoints
    axiosPublic
      .get("[Your Privacy Policy API Endpoint]")
      .then((response) => setTerms_of_service(response.data))
      .catch((error) => setError(error));

    axiosPublic
      .get("[Your Data Security Policy API Endpoint]")
      .then((response) => setData_security_policy(response.data))
      .catch((error) => setError(error));

    axiosPublic
      .get("[Your Cookie Policy API Endpoint]")
      .then((response) => setCookie_policy(response.data))
      .catch((error) => setError(error));
  }, []);
  return (
    <div style={{ padding: "20px", fontFamily: "Arial, sans-serif" }}>
      {error && <p>Error fetching data: {error.message}</p>}
      {terms_of_service && <h1>Terms of Service</h1>}
      {data_security_policy && <h1>Data Security Policy</h1>}
      {cookie_policy && <h1>Cookie Policy</h1>}

      <h1>Privacy Policy</h1>

      <p>Effective Date: {effective_date}</p>

      <section>
        <h2>Introduction</h2>
        <p>
          Welcome to AiPower. We value your privacy and are committed to
          protecting your personal information.
        </p>
      </section>

      <section>
        <h2>Information We Collect</h2>
        <p>
          We collect information to provide better services to our users. This
          includes:
        </p>
        <ul>
          <li>
            Personal Identification Information (e.g., name, email address,
            phone number).
          </li>
          <li>
            Usage Data (e.g., IP address, browser type, and pages visited).
          </li>
        </ul>
      </section>

      <section>
        <h2>How We Use Your Information</h2>
        <p>The information we collect is used to:</p>
        <ul>
          <li>Provide, operate, and maintain our services.</li>
          <li>Improve and personalize user experience.</li>
          <li>Communicate with you regarding updates and support.</li>
        </ul>
      </section>

      <section>
        <h2>Sharing Your Information</h2>
        <p>
          We do not sell your personal data. However, we may share information
          with trusted third-party services to support our operations.
        </p>
      </section>

      <section>
        <h2>Your Rights</h2>
        <p>
          You have the right to access, update, or delete your personal
          information. To exercise these rights, please contact us at [Insert
          Contact Information].
        </p>
      </section>

      <section>
        <h2>Changes to This Privacy Policy</h2>
        <p>
          We may update this privacy policy from time to time. We encourage you
          to review this page periodically for the latest information.
        </p>
      </section>

      <footer>
        <p>
          If you have any questions, please contact us at support@aipower.com.
        </p>
      </footer>
    </div>
  );
};

export default PrivacyPolicy;
