import React, {useEffect, useState} from "react";
import DatePicker from "react-datepicker";
import {BarChart} from '@mui/x-charts/BarChart';
import {getNotesBetweenDates} from "./api";
import {endOfWeek, format, startOfWeek} from 'date-fns';
import '../resources/notes-chart.css';

const formatDate = (date) => {
    return format(date, 'dd-MM-yyyy');
};
const NotesBetweenWeek = () => {
    const [startDate, setStartDate] = useState(startOfWeek(new Date(), {weekStartsOn: 1}));
    const [weeklyGrades, setWeeklyGrades] = useState([]);
    const [error, setError] = useState(null);
    const [endDate, setEndDate] = useState(endOfWeek(new Date(), {weekStartsOn: 1}));

    const handleStartDateChange = (date) => {
        setStartDate(startOfWeek(date, {weekStartsOn: 1}));
        setEndDate(endOfWeek(date, {weekStartsOn: 1}))
    };

    const notesBetweenDates = async () => {
        const formattedStartDate = formatDate(startDate);
        const formattedEndDate = formatDate(endDate);

        try {
            const response = await getNotesBetweenDates(formattedStartDate, formattedEndDate);
            setWeeklyGrades(response);
        } catch (error) {
            setError("Error retrieving the notes");
        }
    };

    useEffect(() => {
        notesBetweenDates();
    }, [startDate]);


    return (
        <div>
            {error && <div className="error" aria-live="assertive" id="errorMessage">{error}</div>}
            <h2>Weekly chart</h2>
            <div className="datepicker-container">

                <div className="rounded-datepicker">
                    <DatePicker id="startDate" value={new Date(startDate).toDateString()}
                                onChange={handleStartDateChange}
                                dateFormat="dd-MM-yyyy" calendarStartDay={1}/>
                </div>

                <div className="rounded-datepicker">
                    <DatePicker id="endDate" value={new Date(endDate).toDateString()} onChange={handleStartDateChange}
                                dateFormat="dd-MM-yyyy" calendarStartDay={1} readOnly/>
                </div>

            </div>

            <div className="barchart-container">
                {weeklyGrades.length === 0 ? (
                    <div>No data to display</div>
                ) : (
                <BarChart
                    series={[
                        {
                            data: weeklyGrades.map(note => note.grade),
                            label: 'Grade',

                        }
                    ]}

                    yAxis={[
                        {
                            colorMap: {
                                type: 'continuous',
                                min: 1,
                                max: 10,
                                color: ['#f44336', '#2db34b'],
                            },
                        },
                    ]}
                    xAxis={[
                        {
                            scaleType: 'band',
                            data: weeklyGrades.map(note => note.date),
                        },
                    ]}

                    height={290}
                />
                )}
            </div>
        </div>
    );
};

export default NotesBetweenWeek;