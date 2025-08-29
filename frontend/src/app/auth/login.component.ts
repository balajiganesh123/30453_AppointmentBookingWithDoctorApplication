import { Component } from '@angular/core';
import { FormBuilder, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { ApiService } from '../services/api.service';
@Component({
  selector: 'app-login',
  templateUrl: './login.component.html'
})
export class LoginComponent {
  f = this.fb.group({ email: ['', [Validators.required, Validators.email]], password: ['', Validators.required] });
  error = '';
  constructor(private fb: FormBuilder, private api: ApiService, private router: Router) {}
  submit() {
    if (this.f.invalid) return;
    this.api.login(this.f.value).subscribe({
      next: (res: any) => {
        localStorage.setItem('user', JSON.stringify(res));
        if (res.role === 'DOCTOR') this.router.navigate(['/doctor', res.userId]);
        else this.router.navigate(['/patient', res.userId]);
      },
      error: () => this.error = 'Invalid credentials'
    });
  }
}
