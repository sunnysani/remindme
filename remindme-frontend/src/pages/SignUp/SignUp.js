import './SignUp.css';
import React, { useState } from 'react';
import { Link, Navigate } from 'react-router-dom';
import { Row } from "react-bootstrap";
import JamKalender from './JamKalender.svg';
import { connect } from 'react-redux';
import { signup as signupAction } from '../../actions/auth';

const SignUp = ({ signup, isAuthenticated }) => {
    console.log(isAuthenticated)
    const [formData, setFormData] = useState({
        first_name: '',
        last_name: '',
        username: '',
        password: ''
    });

    const [registered, setRegister] = useState(false);

    const { first_name, last_name, username, password } = formData;

    const onChange = e => setFormData({ ...formData, [e.target.name]: e.target.value });

    const onSubmit = async e => {
        e.preventDefault();
        const res = await signup(first_name, last_name, username, password);
        console.log(res)
        if (res.signup){
            setRegister(true);
            alert('signed up successfully!');
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
    if (registered === true) {
        return <Navigate to="/login" />
    }

    return (
        <div id="regist-signup">
            <div className="regist-square-box">
                <div className="picture-section">
                    <h3 className="regist-title">Sign Up</h3>
                    <img className="ornament-left" src={JamKalender} alt="Icon Jam Kalender"></img>
                </div>
                <div className="form-section">
                    <form className="regist-centered" onSubmit={e => onSubmit(e)}>
                        <Row className="justify-content-center">
                            <div className="col-sm">
                                <div className="regist-form-container">
                                    <label for='nama-depan'> Nama Depan </label>
                                    <br></br>
                                    <input
                                        id='nama-depan'
                                        type='text'
                                        name='first_name'
                                        value={first_name}
                                        onChange={e => onChange(e)}
                                        pattern="^[A-Za-z]*$"
                                        required
                                    />
                                </div>
                            </div>
                            <div className="col-sm">
                                <div className="regist-form-container">
                                    <label for='nama-belakang'> Nama Belakang </label>
                                    <br></br>
                                    <input
                                        id='nama-belakang'
                                        type='text'
                                        name='last_name'
                                        value={last_name}
                                        onChange={e => onChange(e)}
                                        pattern="^[A-Za-z]*$"
                                        required
                                    />
                                </div>
                            </div>
                        </Row>
                        <Row className="justify-content-center">
                            <div className="col-sm">
                                <div className="regist-form-container">
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
                                <div className="regist-form-container">
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
                            <button className="submit-button remindme-blue-button regist-button" type="submit">
                                Sign Up
                            </button>
                        </Row>
                        <Row className="justify-content-center regist-to-login">
                            <p>Sudah punya akun? <span><Link to='/login'>Masuk disini</Link></span></p>
                        </Row>
                    </form>
                </div>
            </div>
        </div>
    );
}

const mapStateToProps = state => ({
    isAuthenticated: state.auth.isAuthenticated
});

export default connect(mapStateToProps, { signup: signupAction })(SignUp);
