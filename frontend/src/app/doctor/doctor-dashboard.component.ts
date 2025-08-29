import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { FormBuilder, Validators } from '@angular/forms';
import { ApiService } from '../services/api.service';

@Component({
  selector: 'app-doctor-dashboard',
  templateUrl: './doctor-dashboard.component.html'
})
export class DoctorDashboardComponent implements OnInit {
  userId = 0;
  doctorId = 0;
  doctor: any;
  items: any[] = [];
  showAll = false;
  success = '';
  error = '';
  loading = false;
  activeTab: 'appointments' | 'schedule' = 'appointments';
  f = this.fb.group({ date: ['', Validators.required], start: ['', Validators.required], end: ['', Validators.required] });
  todayStr = this.toDateStr(new Date());
  timeMin: string | null = null;
  pastSelection = false;
  invalidRange = false;

  constructor(private route: ActivatedRoute, private api: ApiService, private fb: FormBuilder, private router: Router) {}

  ngOnInit() {
    this.userId = Number(this.route.snapshot.paramMap.get('id'));
    this.api.getDoctorByUser(this.userId).subscribe((d:any) => { this.doctor = d; this.doctorId = d.id; });
    this.f.get('date')?.valueChanges.subscribe(() => { this.updateTimeMin(); this.updateValidityFlags(); });
    this.f.get('start')?.valueChanges.subscribe(() => this.updateValidityFlags());
    this.f.get('end')?.valueChanges.subscribe(() => this.updateValidityFlags());
    this.updateTimeMin();
    this.updateValidityFlags();
    this.load();
  }

  load() {
    if (this.showAll) this.api.doctorAllByUser(this.userId).subscribe((d:any)=> this.items = d);
    else this.api.doctorUpcomingByUser(this.userId).subscribe((d:any)=> this.items = d);
  }

  addSchedule() {
    if (this.f.invalid || !this.doctorId) return;
    this.updateValidityFlags();
    if (this.pastSelection) { this.error = 'Cannot add availability in the past'; return; }
    if (this.invalidRange) { this.error = 'Start time must be earlier than end time'; return; }
    this.success = '';
    this.error = '';
    this.loading = true;
    const body = { date: this.f.value.date, start: this.f.value.start, end: this.f.value.end, slotMinutes: 15 };
    this.api.addSchedule(this.doctorId, body).subscribe({
      next: () => { this.success = 'Slot added successfully'; this.loading = false; this.load(); },
      error: () => { this.error = 'Failed to add slot'; this.loading = false; }
    });
  }

  accept(a:any) {
    if (!window.confirm('Are you sure you want to accept the appointment? This is irreversible.')) return;
    this.api.acceptAppointment(a.id).subscribe({
      next: () => { a.status = 'CONFIRMED'; },
      error: () => {}
    });
  }

  logout() { localStorage.removeItem('user'); this.router.navigate(['/']); }

  private toDateStr(d: Date): string {
    const y = d.getFullYear();
    const m = String(d.getMonth() + 1).padStart(2, '0');
    const dd = String(d.getDate()).padStart(2, '0');
    return `${y}-${m}-${dd}`;
  }

  private nowHm(): string {
    const n = new Date();
    const h = String(n.getHours()).padStart(2, '0');
    const m = String(n.getMinutes()).padStart(2, '0');
    return `${h}:${m}`;
  }

  private updateTimeMin() {
    const d = this.f.value.date || '';
    this.timeMin = d === this.todayStr ? this.nowHm() : null;
  }

  private updateValidityFlags() {
    this.pastSelection = this.isPastSelection();
    this.invalidRange = this.isInvalidRange();
  }

  private isPastSelection(): boolean {
    const d = this.f.value.date as string;
    const s = this.f.value.start as string;
    if (!d || !s) return false;
    const start = new Date(`${d}T${s}:00`);
    const now = new Date();
    if (this.toDateStr(start) < this.todayStr) return true;
    if (this.toDateStr(start) === this.todayStr && start.getTime() < now.getTime()) return true;
    return false;
  }

  private isInvalidRange(): boolean {
    const s = this.f.value.start as string;
    const e = this.f.value.end as string;
    if (!s || !e) return false;
    const [sh, sm] = s.split(':').map(Number);
    const [eh, em] = e.split(':').map(Number);
    const startMin = sh * 60 + sm;
    const endMin = eh * 60 + em;
    return startMin >= endMin;
  }
}