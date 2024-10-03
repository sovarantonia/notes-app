import '../resources/landing-page.css';
import Header from "./header";
import {Link} from "react-router-dom";
import React from "react";
const AboutPage = () => {
    return (
        <div className="landingPageContainer">
            <Header/>
            <div className="contentContainer">
                <h1>Empower Your Personal Growth</h1>
                <p>
                    Whether you're setting goals or reflecting on achievements, our app provides a platform for creating notes that inspire and push you toward personal growth.
                    With a simple and intuitive interface, document your thoughts, goals, and reflections at every step of your journey.
                    Track your progress and see how far you've come, keeping the fire of motivation alive.
                </p>

                <h1>Keep Your Motivation on Track</h1>
                <p>
                    Our app helps you stay committed by offering a variety of tools to track your milestones and visualize your progress.
                    From daily reflections to long-term aspirations, every note brings you closer to achieving your personal best.
                    Motivation is just a note away, reminding you of your growth and keeping you focused.
                </p>

                <h1>Your Notes, Your Motivation—Accessible Anytime</h1>
                <p>
                    No matter where you are, your notes are always within reach.
                    Whether you're at home, in the office, or on the go, access your self-development reflections and motivational reminders across all devices.
                    We believe that progress doesn't stop, and neither should your ability to access the insights that fuel your personal growth.
                </p>

                <h1>Build the Best Version of Yourself</h1>
                <p>
                    Join a growing community of individuals focused on unlocking their potential and becoming the best version of themselves.
                    Use the app not just as a tool but as a partner in your journey to self-improvement.
                    Whether you’re a beginner or already on the path, our app is here to help you succeed and thrive.
                </p>
                <div className="authLinks">
                    <Link to="/login">Start documenting your journey today</Link>
                </div>
            </div>


        </div>
    );
}

export default AboutPage;