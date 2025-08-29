import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html'
})
export class AppComponent implements OnInit {
  isLoggedIn = false;
  role = '';
  userId = 0;

  constructor(private router: Router) {}

  ngOnInit() {
    this.loadUser();
    this.router.events.subscribe(() => this.loadUser());
    window.addEventListener('storage', () => this.loadUser());
  }

  loadUser() {
    const u = localStorage.getItem('user');
    if (u) {
      const obj = JSON.parse(u);
      this.isLoggedIn = true;
      this.role = (obj.role || '').toUpperCase();
      this.userId = Number(obj.userId || obj.id || 0);
    } else {
      this.isLoggedIn = false;
      this.role = '';
      this.userId = 0;
    }
  }

  userLink() {
    if (this.role === 'DOCTOR') return ['/doctor', this.userId];
    return ['/patient', this.userId];
  }

  signOut(e?: Event) {
    e?.preventDefault();
    localStorage.removeItem('user');
    this.loadUser();
    this.router.navigateByUrl('/');
  }
}
