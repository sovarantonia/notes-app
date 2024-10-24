import React, {useEffect, useState} from "react";
import {acceptRequest, declineRequest, getReceivedRequests, getSentRequests} from "./api";
import {useUser} from "./userContext";
import Sidebar from "./sidebar";
import {Button, Table, TableBody, TableCell, TableContainer, TableHead, TableRow} from "@mui/material";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import {faCheck, faTimes} from "@fortawesome/free-solid-svg-icons";
import '../resources/requests-page.css';
import {debounce} from "lodash";

const RequestsPage = () => {
    const {logout} = useUser();
    const [requests, setRequests] = useState([]);
    const [sentRequests, setSentRequests] = useState([]);
    const [error, setError] = useState('');
    const [showTable, setShowTable] = useState(false);

    const toggleTable = () => {
        setShowTable(prevShowTable => !prevShowTable);
    };

    const fetchRequests = async () => {
        try {
            const data = await getReceivedRequests();
            setRequests(data);
        } catch (error) {
            setError('Error fetching requests:');
        }
    }

    const fetchSentRequests = async () => {
        try {
            const data = await getSentRequests();
            setSentRequests(data);
        } catch (error) {
            setError('Error fetching requests:');
        }
    }

    useEffect(() => {
        fetchRequests();
        fetchSentRequests();
    }, []);

    const handleLogout = () => {
        logout();
    };

    const handleAcceptRequest = async (requestId) => {
        try {
            await acceptRequest(requestId);
            debouncedFetchRequests();
        } catch (error) {
            setError('Error accepting the request');
        }
    }

    const handleDeclineRequest = async (requestId) => {
        try {
            await declineRequest(requestId);
            debouncedFetchRequests();
        } catch (error) {
            setError('Error declining the request')
        }
    }

    const debouncedFetchRequests = debounce(() => {
        fetchRequests();
    }, 300);

    return (
        <div className="main-content">
            <div className="requestLists">
                <Sidebar onLogout={handleLogout}/>
                {error && <div className="error" aria-live="assertive" id="errorMessage">{error}</div>}

                <TableContainer id="receivedRequests">
                    <Table sx={{width: '70%', margin: '20px 0', borderCollapse: 'collapse'}}
                           aria-label="received-requests">
                        <TableHead>
                            <TableRow>

                                <TableCell sx={{
                                    backgroundColor: '#F2A2B1',
                                    fontWeight: 'bold',
                                    padding: '12px',
                                    border: '1px solid #ddd'
                                }}>Sender</TableCell>

                                <TableCell sx={{
                                    backgroundColor: '#F2A2B1',
                                    fontWeight: 'bold',
                                    padding: '12px',
                                    border: '1px solid #ddd'
                                }}>Status</TableCell>

                                <TableCell sx={{
                                    backgroundColor: '#F2A2B1',
                                    fontWeight: 'bold',
                                    padding: '12px',
                                    border: '1px solid #ddd'
                                }}>Sent at</TableCell>

                                <TableCell sx={{
                                    backgroundColor: '#F2A2B1',
                                    fontWeight: 'bold',
                                    padding: '12px',
                                    border: '1px solid #ddd'
                                }}>Options</TableCell>

                            </TableRow>
                        </TableHead>
                        <TableBody>
                            {requests.map((item) => (
                                <TableRow
                                    key={item.id}
                                >
                                    <TableCell sx={{
                                        overflow: 'hidden',
                                        textOverflow: 'ellipsis',
                                        whiteSpace: 'nowrap',
                                        padding: '12px',
                                        border: '1px solid #ddd'
                                    }}>{`${item.sender.firstName} ${item.sender.lastName} (${item.sender.email})`}</TableCell>

                                    <TableCell sx={{
                                        overflow: 'hidden',
                                        textOverflow: 'ellipsis',
                                        whiteSpace: 'nowrap',
                                        padding: '12px',
                                        border: '1px solid #ddd'
                                    }}>{item.status}</TableCell>

                                    <TableCell
                                        sx={{padding: '12px', border: '1px solid #ddd'}}>{item.sentAt}</TableCell>

                                    <TableCell sx={{padding: '12px', border: '1px solid #ddd'}}>
                                        <Button id="acceptButton" size="2xl"
                                                onClick={() => handleAcceptRequest(item.id)}><FontAwesomeIcon
                                            icon={faCheck}/>Accept</Button>
                                        <Button id="declineButton" size="2xl"
                                                onClick={() => handleDeclineRequest(item.id)}><FontAwesomeIcon
                                            icon={faTimes}/>Decline</Button>
                                    </TableCell>
                                </TableRow>
                            ))}
                        </TableBody>
                    </Table>
                </TableContainer>

                <div className="dialog-buttons">
                    <Button id="sentRequestTableButton" onClick={toggleTable} variant="contained">
                        {showTable ? 'Hide sent requests' : 'Show sent requests'}
                    </Button>
                </div>

                {showTable && (<TableContainer id="sentRequests">
                        <Table sx={{width: '70%', margin: '20px 0', borderCollapse: 'collapse'}}
                               aria-label="sent-requests">
                            <TableHead>
                                <TableRow>

                                    <TableCell sx={{
                                        backgroundColor: '#F2A2B1',
                                        fontWeight: 'bold',
                                        padding: '12px',
                                        border: '1px solid #ddd'
                                    }}>Receiver</TableCell>

                                    <TableCell sx={{
                                        backgroundColor: '#F2A2B1',
                                        fontWeight: 'bold',
                                        padding: '12px',
                                        border: '1px solid #ddd'
                                    }}>Status</TableCell>

                                    <TableCell sx={{
                                        backgroundColor: '#F2A2B1',
                                        fontWeight: 'bold',
                                        padding: '12px',
                                        border: '1px solid #ddd'
                                    }}>Sent at</TableCell>

                                </TableRow>
                            </TableHead>

                            <TableBody>
                                {sentRequests.map((item) => (
                                    <TableRow
                                        key={item.id}
                                    >
                                        <TableCell sx={{
                                            overflow: 'hidden',
                                            textOverflow: 'ellipsis',
                                            whiteSpace: 'nowrap',
                                            padding: '12px',
                                            border: '1px solid #ddd'
                                        }}>{`${item.receiver.firstName} ${item.receiver.lastName} (${item.receiver.email})`}</TableCell>
                                        <TableCell sx={{
                                            overflow: 'hidden',
                                            textOverflow: 'ellipsis',
                                            whiteSpace: 'nowrap',
                                            padding: '12px',
                                            border: '1px solid #ddd'
                                        }}>{item.status}</TableCell>
                                        <TableCell
                                            sx={{padding: '12px', border: '1px solid #ddd'}}>{item.sentAt}</TableCell>

                                    </TableRow>
                                ))}
                            </TableBody>
                        </Table>
                    </TableContainer>
                )}
            </div>
        </div>

    )
}

export default RequestsPage;