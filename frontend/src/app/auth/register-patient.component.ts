import { Component } from '@angular/core';
import { FormBuilder, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { ApiService } from '../services/api.service';

@Component({
  selector: 'app-register-patient',
  templateUrl: './register-patient.component.html'
})
export class RegisterPatientComponent {
  f = this.fb.group({
    name: ['', [Validators.required, Validators.pattern(/^[A-Za-z\s]+$/)]],
    age: ['', [Validators.required, Validators.pattern(/^(?:[1-9]\d?|100)$/)]],
    phone: ['', [Validators.required, Validators.pattern(/^\d{10}$/)]],
    gender: [''],
    email: ['', [Validators.required, Validators.pattern(/^(?!\.)(?!.*\.\.)[A-Za-z0-9.]+(?<!\.)@example\.com$/)]],
    password: ['', [Validators.required]]
  });

  constructor(private fb: FormBuilder, private api: ApiService, private router: Router) {}

  submit() {
    if (this.f.invalid) return;
    const v: any = this.f.value;
    const payload = { ...v, age: Number(v.age) };
    this.api.registerPatient(payload).subscribe(() => this.router.navigate(['/login']));
  }
}
