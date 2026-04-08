import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, ActivatedRoute } from '@angular/router';
import { MatCardModule } from '@angular/material/card';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    MatCardModule,
    MatInputModule,
    MatButtonModule,
    MatIconModule,
    MatProgressSpinnerModule,
    MatSnackBarModule
  ],
  template: `
    <div class="login-wrapper">
      <mat-card class="login-card">
        <mat-card-header>
          <mat-card-title>
            <mat-icon color="primary">assessment</mat-icon>
            报表管理系统
          </mat-card-title>
          <mat-card-subtitle>请登录以继续</mat-card-subtitle>
        </mat-card-header>

        <mat-card-content>
          <div *ngIf="authService.isLoggedIn(); else loginForm" class="logged-in-section">
            <p class="success-message">
              <mat-icon color="primary">check_circle</mat-icon>
              已登录: {{ authService.getCurrentUser()?.username }}
            </p>
            <button mat-raised-button color="primary" (click)="goAfterLogin()">
              进入系统
            </button>
          </div>

          <ng-template #loginForm>
            <form (ngSubmit)="onSubmit()" class="login-form">
              <mat-form-field appearance="outline" class="full-width">
                <mat-label>用户名</mat-label>
                <input matInput [(ngModel)]="username" name="username" required
                       placeholder="请输入用户名">
                <mat-icon matPrefix>person</mat-icon>
              </mat-form-field>

              <mat-form-field appearance="outline" class="full-width">
                <mat-label>密码</mat-label>
                <input matInput [(ngModel)]="password" name="password" required
                       type="password" placeholder="请输入密码">
                <mat-icon matPrefix>lock</mat-icon>
              </mat-form-field>

              <div class="error-message" *ngIf="loginError">
                <mat-icon color="warn">error</mat-icon>
                {{ loginError }}
              </div>

              <button mat-raised-button color="primary" type="submit"
                      [disabled]="loggingIn" class="full-width login-button">
                <mat-spinner diameter="20" *ngIf="loggingIn"></mat-spinner>
                <span *ngIf="!loggingIn">登录</span>
              </button>
            </form>
          </ng-template>
        </mat-card-content>
      </mat-card>
    </div>
  `,
  styles: [`
    .login-wrapper {
      display: flex;
      justify-content: center;
      align-items: center;
      min-height: calc(100vh - 64px);
      background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
      padding: 24px;
    }

    .login-card {
      width: 100%;
      max-width: 400px;
      border-radius: 12px;
      box-shadow: 0 8px 32px rgba(0,0,0,0.2);
    }

    mat-card-header {
      justify-content: center;
      text-align: center;
      padding-top: 24px;
    }

    mat-card-title {
      display: flex;
      align-items: center;
      justify-content: center;
      gap: 8px;
      font-size: 24px !important;
      font-weight: 500 !important;
    }

    mat-card-subtitle {
      text-align: center;
      margin-top: 8px !important;
    }

    .login-form {
      display: flex;
      flex-direction: column;
      gap: 16px;
      margin-top: 24px;
    }

    .full-width {
      width: 100%;
    }

    .login-button {
      height: 48px;
      font-size: 16px;
      margin-top: 8px;
      display: flex;
      align-items: center;
      justify-content: center;
      gap: 8px;
    }

    .error-message {
      display: flex;
      align-items: center;
      gap: 8px;
      color: #f44336;
      font-size: 14px;
      padding: 8px;
      background: #ffebee;
      border-radius: 4px;
    }

    .logged-in-section {
      text-align: center;
      padding: 24px;
    }

    .success-message {
      display: flex;
      align-items: center;
      justify-content: center;
      gap: 8px;
      color: #4caf50;
      font-size: 16px;
      margin-bottom: 24px;
    }
  `]
})
export class LoginComponent implements OnInit {
  username = '';
  password = '';
  loggingIn = false;
  loginError: string | null = null;
  private redirectUrl: string | null = null;

  constructor(
    public authService: AuthService,
    private router: Router,
    private route: ActivatedRoute,
    private snackBar: MatSnackBar
  ) {}

  ngOnInit(): void {
    this.redirectUrl = this.route.snapshot.queryParamMap.get('redirect');
  }

  onSubmit(): void {
    if (!this.username || !this.password) {
      this.loginError = '请输入用户名和密码';
      return;
    }
    this.loggingIn = true;
    this.loginError = null;

    this.authService.login(this.username, this.password).subscribe({
      next: () => {
        this.loggingIn = false;
        this.snackBar.open('登录成功！', '确定', { duration: 3000 });
        this.goAfterLogin();
      },
      error: (err) => {
        this.loggingIn = false;
        this.loginError = '登录失败: ' + (err.error?.message || err.message || '');
      }
    });
  }

  goAfterLogin(): void {
    const user = this.authService.getCurrentUser();
    let defaultTarget = '/reports';
    const role = user?.role || '';
    if (role.includes('CHECKER')) {
      defaultTarget = '/checker';
    } else if (role.includes('MAKER')) {
      defaultTarget = '/maker';
    }
    const target = this.redirectUrl || defaultTarget;
    this.router.navigateByUrl(target);
  }
}
