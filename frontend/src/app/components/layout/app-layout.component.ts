import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Router } from '@angular/router';
import { MatToolbarModule } from '@angular/material/toolbar';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatMenuModule } from '@angular/material/menu';
import { MatSidenavModule } from '@angular/material/sidenav';
import { MatListModule } from '@angular/material/list';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-layout',
  standalone: true,
  imports: [
    CommonModule,
    RouterModule,
    MatToolbarModule,
    MatButtonModule,
    MatIconModule,
    MatMenuModule,
    MatSidenavModule,
    MatListModule
  ],
  template: `
    <mat-toolbar color="primary" class="app-toolbar">
      <span class="app-title">
        <mat-icon>assessment</mat-icon>
        报表管理系统
      </span>
      <span class="spacer"></span>

      <ng-container *ngIf="authService.isLoggedIn(); else loginButton">
        <span class="user-info">
          <mat-icon>person</mat-icon>
          {{ authService.getCurrentUser()?.username }}
          ({{ getRoleDisplay() }})
        </span>
        <button mat-icon-button [matMenuTriggerFor]="userMenu">
          <mat-icon>more_vert</mat-icon>
        </button>
        <mat-menu #userMenu="matMenu">
          <button mat-menu-item (click)="logout()">
            <mat-icon>logout</mat-icon>
            <span>退出登录</span>
          </button>
        </mat-menu>
      </ng-container>

      <ng-template #loginButton>
        <button mat-raised-button color="accent" routerLink="/login">
          <mat-icon>login</mat-icon>
          登录
        </button>
      </ng-template>
    </mat-toolbar>

    <div class="app-content">
      <router-outlet></router-outlet>
    </div>
  `,
  styles: [`
    .app-toolbar {
      display: flex;
      align-items: center;
      padding: 0 24px;
      box-shadow: 0 2px 4px rgba(0,0,0,0.1);
    }

    .app-title {
      display: flex;
      align-items: center;
      gap: 8px;
      font-size: 20px;
      font-weight: 500;
    }

    .spacer {
      flex: 1;
    }

    .user-info {
      display: flex;
      align-items: center;
      gap: 4px;
      font-size: 14px;
      margin-right: 16px;
    }

    .app-content {
      min-height: calc(100vh - 64px);
      background: #f5f5f5;
    }
  `]
})
export class AppLayoutComponent {
  constructor(
    public authService: AuthService,
    private router: Router
  ) {}

  getRoleDisplay(): string {
    const user = this.authService.getCurrentUser();
    if (!user?.role) return '';

    const roleMap: { [key: string]: string } = {
      'MAKER': '制表员',
      'CHECKER': '审核员'
    };

    return roleMap[user.role] || user.role;
  }

  logout(): void {
    this.authService.logout();
    this.router.navigate(['/login']);
  }
}
