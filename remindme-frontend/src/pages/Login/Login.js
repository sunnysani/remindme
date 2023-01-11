import './Login.css';
import React, { useState } from 'react';
import { Link, Navigate } from 'react-router-dom';
import { Row } from "react-bootstrap";
import JamKalender from './JamKalender.svg'
import { connect } from 'react-redux';
import axios from 'axios';
import { login as loginAction } from '../../actions/auth';

const Login = ({ login, isAuthenticated }) => {
    const [formData, setFormData] = useState({
        username: '',
        password: ''
    });

    const { username, password } = formData;

    const onChange = e => setFormData({ ...formData, [e.target.name]: e.target.value });

    const onSubmit = async e => {
        e.preventDefault();
        const res = await login(username, password);
        console.log(res)
        if (res.login){
            console.log(isAuthenticated);
            alert('logged in successfully!');
        } else {
            console.log(res.err.response.data);
            var err_response = res.err.response.data;
            var [first_key_error] = Object.keys(err_response);
            var val_error = err_response[first_key_error];
            alert(val_error);
        }
    };

    if (isAuthenticated) {
        return <Navigate to='/' />
    }

    return (
        <div id="login-signup">
            <div className="login-square-box mt-5">
                <div className="picture-section">
                    <h3 className="login-title">Log In</h3>
                    <img className="ornament-left" src={JamKalender} alt="Icon Jam Kalender"></img>
                </div>
                <div className="form-section">
                    <form className="login-centered" onSubmit={e => onSubmit(e)}>
                        <Row className="justify-content-center">
                            <div className="col-sm">
                                <div className="login-form-container">
                                    <label for='username'> Username </label>
                                    <br></br>
                                    <input
                                        id='username'
                                        type='username'
                                        name='username'
                                        value={username}
                                        onChange={e => onChange(e)}
                                        required
                                    />
                                </div>
                            </div>
                        </Row>
                        <Row className="justify-content-center">
                            <div className="col-sm">
                                <div className="login-form-container">
                                    <label for='i_password'> Password </label>
                                    <br></br>
                                    <input
                                        id='i_password'
                                        type='password'
                                        name='password'
                                        value={password}
                                        onChange={e => onChange(e)}
                                        pattern="^(?=.*[A-Za-z])(?=.*\d)(?=.*[,./:;^@$!%*#?&])[A-Za-z\d,./:;^@$!%*#?&]{8,}$"
                                        title="Must contain at least one number, one uppercase letter, one lowercase letter, one special character ',./:;^@$!%*#?&', and at least have 8 or more characters"
                                        required
                                    />
                                </div>
                            </div>
                        </Row>
                        <Row className="justify-content-center">
                            <button className="submit-button remindme-blue-button login-button" type="submit">
                                Login
                            </button>
                        </Row>
                        <Row className="justify-content-center login-to-login">
                            <p>Sudah punya akun? <span><Link to='/signup'>Masuk disini</Link></span></p>
                        </Row>
                    </form>
                </div>
            </div>
        </div>
    );
}

const mapStateToProps = state => ({
    isAuthenticated: state.auth.isAuthenticated
})

export default connect(mapStateToProps, { login: loginAction })(Login);
