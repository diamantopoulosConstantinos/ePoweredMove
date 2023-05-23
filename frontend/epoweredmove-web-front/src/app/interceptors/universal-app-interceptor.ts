import {AuthService} from "../services/auth.service";
import {HttpEvent, HttpHandler, HttpInterceptor, HttpRequest} from "@angular/common/http";
import {Injectable} from "@angular/core";
import {finalize, from, Observable, switchMap, tap} from "rxjs";
import {LoaderService} from "../services/loader.service";

@Injectable()
export class UniversalAppInterceptor implements HttpInterceptor {
  private totalRequests = 0;
  constructor( private authService: AuthService,
               private loadingService: LoaderService) { }

  intercept(request: HttpRequest<any>, next: HttpHandler) : Observable<HttpEvent<any>>{
    return from(this.authService.refreshIdToken())
      .pipe(
        switchMap(idToken => {
          this.totalRequests++;
          this.loadingService.setLoading(true);
          request = request.clone({
            url:  request.url,
            setHeaders: {
              Authorization: idToken ? idToken : ""
            }
          });

          return next.handle(request).pipe(
            finalize(() => {
              this.totalRequests--;
              if (this.totalRequests == 0) {
                this.loadingService.setLoading(false);
              }
            })
          );
        })
      );
  }
}
