import React, { useState } from 'react';
import '../Reminder/Reminder.css';
import TrashIcon from '../Reminder/TrashIcon.svg';
import CreateIcon from '../Reminder/CreateIcon.svg';
import { connect } from 'react-redux';
import axios from 'axios';
import { Modal, Button, Form, Row, Col } from 'react-bootstrap';

const Schedule = ({ user, isAuthenticatd }) => {

    const [schedulers, setSchedulers] = useState([])
    const [show, setShow] = useState(false);
    const [scheduleChg, setScheduleChg] = useState(false)
    const [modalId, setModalId] = useState(-1)

    React.useEffect(() => {
        axios.get(`https://cloud.dirtboll.com:8686/api/scheduler/all`, {
            headers: {
                'Authorization': `Bearer ${localStorage.getItem('access')}`
            }
        }).then(resp => {
            setSchedulers(resp.data)
        })
            .catch(err => console.error(err))
    }, [scheduleChg])

    // Delete Schedule
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
        axios.delete(`https://cloud.dirtboll.com:8686/api/scheduler/${modalId}`, {
            headers: {
                'Authorization': `Bearer ${localStorage.getItem('access')}`
            }
        }).then(() => {
            setShow(false)
            setScheduleChg(!scheduleChg)
        })
    }

    // Create Scheduler
    const [showCreate, setShowCreate] = useState(false);
    const [formData, setFormData] = useState({
        activity_name: '',
        schedule_time: 0,
        schedule_interval: ''
    })
    const onChangeCreate = (e) => {
        setFormData({ ...formData, [e.target.name]: e.target.value });
    }
    const handleCloseCreate = () => setShowCreate(false);
    const handleShowCreate = () => setShowCreate(true);

    const handleSubmitCreate = (e) => {
        e.preventDefault();
        formData.schedule_time = parseInt(formData.schedule_time) || 1
        axios.post("https://cloud.dirtboll.com:8686/api/scheduler", formData, {
            headers: {
                'Authorization': `Bearer ${localStorage.getItem('access')}`
            }
        }).then(() => {
            setShowCreate(false)
            setScheduleChg(!scheduleChg)
        }).catch(e => {
            console.error(e)
        })
    }

    return (
        <div className="remindme-notification-scheduler">
            <h4 className="remindme-notification-title">Scheduler</h4>
            {
                schedulers.length !== 0 ? schedulers.map((v) => (
                    <div key={v.id} className="remindme-notification-item">
                        <div className="remindme-notification-item-content">
                            <p className="remindme-notification-item-title">
                                {v.activity_name}
                            </p>
                            <p className="remindme-notification-item-subtitle">
                                Every {v.schedule_time} {v.schedule_time > 1 ? v.schedule_interval : v.schedule_interval.slice(0, -1)}
                            </p>
                        </div>
                        <div onClick={handleShow(v.id)} className="remindme-notification-item-delete">
                            <img src={TrashIcon} alt="Delete Schedule" />
                        </div>
                    </div>
                )) : <p className='remindme-notification-nothing'>There's nothing in here</p>
            }
            <div className="d-flex flex-row justify-content-end remindme-notification-item-create">
                <img style={{ width: "32px" }} onClick={handleShowCreate} src={CreateIcon} alt="Create Schedule" />
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
                        Do you really want to delete this scheduler? This process cannot be undone
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
                            Add Schedule Item
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
                        <Form.Group as={Row} className="mb-3" controlId="formInterval">
                            <Form.Label column sm={2}>
                                Interval
                            </Form.Label>
                            <Col sm={10}>
                                <Form.Control
                                    type="number"
                                    name="schedule_time"
                                    onChange={onChangeCreate}
                                    min="1"
                                    required
                                />
                            </Col>
                        </Form.Group>
                        <Form.Group as={Row} className="mb-3" controlId="formDate">
                            <Form.Label column sm={2}>
                                Time
                            </Form.Label>
                            <Col sm={10}>
                                <Form.Select
                                    aria-label="Interval Time"
                                    name="schedule_interval"
                                    onChange={onChangeCreate}
                                    required
                                    defaultValue={""}
                                >
                                    <option disabled="disabled" value="">Select interval type</option>
                                    <option value="Minutes">Minutes</option>
                                    <option value="Hours">Hours</option>
                                    <option value="Days">Days</option>
                                    <option value="Weeks">Weeks</option>
                                    <option value="Months">Months</option>
                                </Form.Select>
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

export default connect(mapStateToProps)(Schedule);