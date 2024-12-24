import React from "react";

const PressContact = () => {
  return (
    <div className="press-contact">
      <h1>Press Contact</h1>
      <p>
        For media inquiries, please contact our press team. We are happy to
        provide information, statements, and other resources.
      </p>

      <div className="contact-info">
        <h2>Contact Information</h2>
        <p>
          <strong>Email:</strong> press@yourcompany.com
        </p>
        <p>
          <strong>Phone:</strong> +1 (555) 123-4567
        </p>
        <p>
          <strong>Address:</strong>
        </p>
        <p>
          Your Company Name
          <br />
          1234 Media Lane
          <br />
          City, State, ZIP Code
        </p>
      </div>

      <div className="additional-resources">
        <h2>Additional Resources</h2>
        <ul>
          <li>
            <a href="/media-kit" target="_blank" rel="noopener noreferrer">
              Media Kit
            </a>
          </li>
          <li>
            <a href="/press-releases" target="_blank" rel="noopener noreferrer">
              Press Releases
            </a>
          </li>
          <li>
            <a href="/about-us" target="_blank" rel="noopener noreferrer">
              About Us
            </a>
          </li>
        </ul>
      </div>
    </div>
  );
};

export default PressContact;
