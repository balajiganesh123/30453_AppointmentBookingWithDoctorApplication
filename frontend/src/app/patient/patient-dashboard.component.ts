import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { ApiService } from '../services/api.service';

@Component({
  selector: 'app-patient-dashboard',
  templateUrl: './patient-dashboard.component.html'
})
export class PatientDashboardComponent implements OnInit {
  userId = 0;
  patient: any = null;
  items: any[] = [];
  loading = false;
  error = '';

  constructor(private route: ActivatedRoute, private api: ApiService) {}

  ngOnInit(): void {
    this.userId = Number(this.route.snapshot.paramMap.get('id'));
    const u = localStorage.getItem('user');
    this.patient = u ? JSON.parse(u) : null;
    this.load();
  }

  load(): void {
    this.loading = true;
    this.api.patientAppointments(this.userId).subscribe({
      next: (r: any[]) => { this.items = r; this.loading = false; },
      error: () => { this.error = 'Failed to load'; this.loading = false; }
    });
  }

  cancel(a: any): void {
    if (a.status !== 'PENDING') return;
    if (!window.confirm('Are you sure you want to cancel this appointment? This is irreversible.')) return;
    this.api.cancelAppointment(a.id).subscribe({
      next: () => { a.status = 'CANCELLED'; },
      error: () => {}
    });
  }
}