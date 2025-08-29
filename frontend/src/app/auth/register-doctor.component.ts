import { Component } from '@angular/core';
import { FormBuilder, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { ApiService } from '../services/api.service';

@Component({
  selector: 'app-register-doctor',
  templateUrl: './register-doctor.component.html'
})
export class RegisterDoctorComponent {
  f = this.fb.group({
    name: ['', [Validators.required, Validators.pattern(/^[A-Za-z\s]+$/)]],
    email: ['', [Validators.required, Validators.pattern(/^(?!\.)(?!.*\.\.)[A-Za-z0-9.]+(?<!\.)@example\.com$/)]],
    password: ['', [Validators.required]],
    age: ['', [Validators.required, Validators.pattern(/^(?:[1-9]\d?|100)$/)]],
    phone: ['', [Validators.required, Validators.pattern(/^\d{10}$/)]],
    gender: [''],
    degrees: [''],
    experienceYears: [null],
    specialization: [''],
    city: [''],
    clinicLocation: ['']
  });

  constructor(private fb: FormBuilder, private api: ApiService, private router: Router) {}

  submit() {
    if (this.f.invalid) return;
    const v: any = this.f.value;
    const payload = { ...v, age: Number(v.age) };
    this.api.registerDoctor(payload).subscribe(() => this.router.navigate(['/login']));
  }
}
