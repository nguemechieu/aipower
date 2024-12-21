from setuptools import setup, find_packages

# Read the long description from README.md
with open("README.md", "r", encoding="utf-8") as fh:
    long_description = fh.read()

# Dynamic load install_requires from requirements.txt
def load_requirements(filename):
    with open(filename, "r") as file:
        return file.read().splitlines()

setup(
    name="assb",  # Package name
    version="3.0.1",  # Current version
    author="Noel Nguemechieu",
    author_email="nguemechieu@live.com",
    description="An intuitive trading bot leveraging sentiment analysis and technical indicators",
    long_description=long_description,
    long_description_content_type="text/markdown",
    url="https://github.com/nguemechieu/aipower/assb",  # Replace with your repo
    packages=find_packages(),  # Automatically find packages
    classifiers=[
        "Programming Language :: Python :: 3",
        "License :: OSI Approved :: MIT License",
        "Operating System :: OS Independent",
    ],
    python_requires=">=3.7",  # Minimum Python version required
    install_requires=load_requirements("requirements.txt"),  # Load dependencies from requirements.txt
    entry_points={
        "console_scripts": [
            "assb_bot=bot:assb_bot",  # Expose the main bot function as a CLI command
        ],
    },
    include_package_data=True,  # Include additional files specified in MANIFEST.in
    project_urls={
        "Bug Tracker": "https://github.com/nguemechieu/aipower/assb/issues",
        "Documentation": "https://github.com/nguemechieu/aipower/assb/wiki",
        "Source Code": "https://github.com/nguemechieu/aipower/assb",
        "Funding": "https://github.com/nguemechieu/aipower/assb/funding",
        "Say Thanks!": "https://saythanks.io/to/nguemechieu",
    },
)
