document.addEventListener('DOMContentLoaded', function() {
    const reservationForm = document.getElementById('reservation-form');

    if (!reservationForm) {
        console.error("The reservation form element was not found.");
        return;
    }

    reservationForm.addEventListener('submit', function(event) {
        event.preventDefault();

        const nameInput = document.getElementById('name');
        const dateInput = document.getElementById('date');
        const timeInput = document.getElementById('time');
        const guestsInput = document.getElementById('guests');

        if (!nameInput || !dateInput || !timeInput || !guestsInput) {
            console.error("One or more form input elements were not found.");
            return;
        }

        // Get form values
        const name = nameInput.value;
        const date = dateInput.value;
        const time = timeInput.value;
        const guests = guestsInput.value;

        // Format the reservation details
        const reservationDetails = `
            Name: ${name}
            Date: ${date}
            Time: ${time}
            Guests: ${guests}
        `;

        alert('Reservation Details:\n\n' + reservationDetails);
        
        // Reset the form
        reservationForm.reset();
    });
});
