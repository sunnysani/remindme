import React, { useState } from 'react';
import './Reminder.css';
import TrashIcon from './TrashIcon.svg';
import CreateIcon from './CreateIcon.svg';
import { connect } from 'react-redux';
import axios from 'axios';
import { Modal, Button, Form, Row, Col } from 'react-bootstrap';

const Reminder = ({ user, isAuthenticatd }) => {

    const [reminders, setReminders] = useState([])
    const [show, setShow] = useState(false);
    const [reminderChg, setReminderChg] = useState(false)
    const [modalId, setModalId] = useState(-1)

    React.useEffect(() => {
        axios.get(`https://cloud.dirtboll.com:8686/api/reminder/all`, {
            headers: {
                'Authorization': `Bearer ${localStorage.getItem('access')}`
            }
        }).then(resp => {
            setReminders(resp.data.data)
        })
            .catch(err => console.error(err))
    }, [reminderChg])

    // Delete Reminder
    const handleClose = () => setShow(false);
    const handleShow = (id) => {
        return () => {
            setModalId(id)
            setShow(true)
        }
    };
    function handleDelete() {
        if (modalId < 0) {
            setShow(false)
            return
        }
        axios.delete(`https://cloud.dirtboll.com:8686/api/reminder/${modalId}`, {
            headers: {
                'Authorization': `Bearer ${localStorage.getItem('access')}`
            }
        }).then(() => {
            setShow(false)
            setReminderChg(!reminderChg)
        })
    }

    // Create Reminder
    const [showCreate, setShowCreate] = useState(false);
    const [formData, setFormData] = useState({
        activity_name: '',
        remind_time: ''
    })
    const onChangeCreate = (e) => {
        setFormData({ ...formData, [e.target.name]: e.target.value });
    }
    const handleCloseCreate = () => setShowCreate(false);
    const handleShowCreate = () => setShowCreate(true);

    const handleSubmitCreate = (e) => {
        e.preventDefault();
        formData.remind_time = new Date(formData.remind_time).toISOString()
        axios.post("https://cloud.dirtboll.com:8686/api/reminder", formData, {
            headers: {
                'Authorization': `Bearer ${localStorage.getItem('access')}`
            }
        }).then(() => {
            setShowCreate(false)
            setReminderChg(!reminderChg)
        }).catch(e => {
            console.error(e)
        })
    }

    return (
        <div className="remindme-notification-reminder">
            <h4 className="remindme-notification-title">Reminder</h4>
            {
                reminders.length !== 0 ? reminders.map((v) => (
                    <div key={v.id} className="remindme-notification-item">
                        <div className="remindme-notification-item-content">
                            <p className="remindme-notification-item-title">
                                {v.activity_name}
                            </p>
                            <p className="remindme-notification-item-subtitle">
                                {(new Date(v.remind_time)).toLocaleString()}
                            </p>
                        </div>
                        <div onClick={handleShow(v.id)} className="remindme-notification-item-delete">
                            <img src={TrashIcon} alt="Delete Reminder" />
                        </div>
                    </div>
                )) : <p className='remindme-notification-nothing'>There's nothing in here</p>
            }
            <div className="d-flex flex-row justify-content-end remindme-notification-item-create">
                <img style={{ width: "32px" }} onClick={handleShowCreate} src={CreateIcon} alt="Create Reminder" />
            </div>

            <Modal show={show} onHide={handleClose}>
                <Modal.Header>
                    <div className="remindme-modal-header-div">
                        <h3 className="remindme-modal-header">
                            Are you sure?
                        </h3>
                    </div>
                </Modal.Header>
                <Modal.Body>
                    <p className="remindme-modal-body">
                        Do you really want to delete this reminder? This process cannot be undone
                    </p>
                </Modal.Body>
                <Modal.Footer>
                    <Button variant="secondary" onClick={handleClose}>
                        Cancel
                    </Button>
                    <Button variant="danger" onClick={handleDelete}>
                        Delete
                    </Button>
                </Modal.Footer>
            </Modal>

            <Modal show={showCreate} onHide={handleCloseCreate} centered>
                <Modal.Header>
                    <div className="remindme-modal-header-div">
                        <h3 className="remindme-modal-header">
                            Add Reminder Item
                        </h3>
                    </div>
                </Modal.Header>
                <Modal.Body>
                    <Form className="p-2" onSubmit={handleSubmitCreate}>
                        <Form.Group as={Row} className="mb-3" controlId="formActivityName">
                            <Form.Label column sm={2}>
                                Activity
                            </Form.Label>
                            <Col sm={10}>
                                <Form.Control
                                    type="text"
                                    name="activity_name"
                                    onChange={onChangeCreate}
                                    required
                                />
                            </Col>
                        </Form.Group>
                        <Form.Group as={Row} className="mb-3" controlId="formDate">
                            <Form.Label column sm={2}>
                                Date
                            </Form.Label>
                            <Col sm={10}>
                                <Form.Control
                                    type="datetime-local"
                                    name="remind_time"
                                    onChange={onChangeCreate}
                                    min={new Date().toISOString().slice(0, 16)}
                                    required
                                />
                            </Col>
                        </Form.Group>
                        <div className="d-flex flex-row justify-content-end">
                            <Button variant="secondary" onClick={handleCloseCreate}>Cancel</Button>
                            <Button variant="primary" className="ml-2" type="submit">Submit form</Button>
                        </div>
                    </Form>
                </Modal.Body>
            </Modal>
        </div>
    )
}

const mapStateToProps = (state) => ({
    isAuthenticated: state.auth.isAuthenticated,
    user: state.auth.user
})

export default connect(mapStateToProps)(Reminder);