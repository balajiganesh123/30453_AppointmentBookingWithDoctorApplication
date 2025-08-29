import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HttpClientModule } from '@angular/common/http';
import { AppointmentService, Appointment } from '../../services/appointment.service';

@Component({
  selector: 'app-doctor-appointments',
  standalone: true,
  imports: [CommonModule, HttpClientModule],
  templateUrl: './doctor-appointments.component.html'
})
export class DoctorAppointmentsComponent implements OnInit {
  items: Appointment[] = [];
  showAll = false;
  loading = false;

  constructor(private api: AppointmentService) {}

  get userId(): number {
    return Number(localStorage.getItem('userId'));
  }

  ngOnInit(): void {
    this.load();
  }

  load(): void {
    this.loading = true;
    const req = this.showAll ? this.api.doctorAll(this.userId) : this.api.doctorUpcoming(this.userId);
    req.subscribe({
      next: r => { this.items = r; this.loading = false; },
      error: () => { this.loading = false; }
    });
  }

  accept(a: Appointment): void {
    if (!window.confirm('Are you sure you want to accept the appointment? This is irreversible.')) return;
    this.api.accept(a.id).subscribe({
      next: () => { a.status = 'ACCEPTED'; },
      error: () => {}
    });
  }

  complete(a: Appointment): void {
    if (!window.confirm('Are you sure you want to mark as completed? This is irreversible.')) return;
    this.api.complete(a.id).subscribe({
      next: () => { a.status = 'COMPLETED'; },
      error: () => {}
    });
  }
}
