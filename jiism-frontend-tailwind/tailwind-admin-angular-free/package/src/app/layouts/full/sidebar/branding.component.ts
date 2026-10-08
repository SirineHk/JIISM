import { Component } from '@angular/core';
import { RouterLink } from '@angular/router';

@Component({
  selector: 'app-branding',
  standalone: true,
  template: `
    <a
      routerLink="/admin/dashboard"
      class="flex items-center gap-3 px-4 py-5 no-underline"
    >
      <div
        class="w-10 h-10 rounded-xl bg-indigo-600 text-white
               flex items-center justify-center font-black text-lg shadow-lg"
      >
        J
      </div>

      <div>
        <div class="text-xl font-black text-slate-800">
          JIISM
        </div>

        <div class="text-[9px] uppercase tracking-widest text-slate-400">
          Journal scientifique
        </div>
      </div>
    </a>
  `,
  imports: [RouterLink]
})
export class BrandingComponent {}