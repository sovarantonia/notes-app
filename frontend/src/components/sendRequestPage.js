import {useState} from "react";
import {sendRequest} from "./api";

const sendRequestPage = (senderId) => {
    const [error, setError] = useState('')
    const sendRequest = async (senderId, receiverEmail) => {
        try {
            await sendRequest(senderId, receiverEmail);
        }catch (error){
            setError("Error sending the request");
        }
    }


}