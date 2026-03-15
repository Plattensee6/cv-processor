import { useState } from 'react';
import { Link, useNavigate } from 'react-router';
import { Button } from '../../components/ui/button';
import { Input } from '../../components/ui/input';
import { Label } from '../../components/ui/label';
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '../../components/ui/card';
import { useAuth } from '../../context/AuthContext';
import { Alert, AlertDescription } from '../../components/ui/alert';

export function ForgotPassword() {
  const { forgotPassword } = useAuth();
  const navigate = useNavigate();
  const [email, setEmail] = useState('');
  const [error, setError] = useState('');
  const [sent, setSent] = useState(false);
  const [resetToken, setResetToken] = useState<string | null>(null);
  const [loading, setLoading] = useState(false);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError('');
    setLoading(true);
    try {
      const { resetToken: token } = await forgotPassword(email);
      setSent(true);
      if (token) setResetToken(token);
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Kérés sikertelen');
    } finally {
      setLoading(false);
    }
  };

  if (sent) {
    return (
      <div className="min-h-screen flex items-center justify-center bg-gray-50 px-4">
        <Card className="w-full max-w-md">
          <CardHeader className="space-y-1 text-center">
            <CardTitle className="text-2xl">Levél elküldve</CardTitle>
            <CardDescription>
              Ha létezik ilyen fiók, a jelszó visszaállításához használd az alábbi gombot.
            </CardDescription>
          </CardHeader>
          <CardContent className="space-y-4">
            {resetToken ? (
              <Button
                className="w-full"
                onClick={() => navigate(`/admin/reset-password?token=${encodeURIComponent(resetToken)}`, { replace: true })}
              >
                Új jelszó megadása
              </Button>
            ) : (
              <p className="text-sm text-gray-600 text-center">
                Ha nem kaptál e-mailt, ellenőrizd a spam mappát vagy próbáld újra később.
              </p>
            )}
            <p className="text-center text-sm">
              <Link to="/admin/login" className="text-blue-600 hover:underline">
                Vissza a bejelentkezéshez
              </Link>
            </p>
          </CardContent>
        </Card>
      </div>
    );
  }

  return (
    <div className="min-h-screen flex items-center justify-center bg-gray-50 px-4">
      <Card className="w-full max-w-md">
        <CardHeader className="space-y-1 text-center">
          <div className="mx-auto w-10 h-10 bg-blue-600 rounded-lg flex items-center justify-center mb-2">
            <span className="text-white font-semibold text-sm">HR</span>
          </div>
          <CardTitle className="text-2xl">Elfelejtett jelszó</CardTitle>
          <CardDescription>
            Add meg az e-mail címed, és a jelszó visszaállítási linket megkapod (vagy itt folytathatod).
          </CardDescription>
        </CardHeader>
        <CardContent>
          <form onSubmit={handleSubmit} className="space-y-4">
            {error && (
              <Alert variant="destructive">
                <AlertDescription>{error}</AlertDescription>
              </Alert>
            )}
            <div className="space-y-2">
              <Label htmlFor="email">E-mail</Label>
              <Input
                id="email"
                type="email"
                placeholder="admin@ceg.hu"
                value={email}
                onChange={(e) => setEmail(e.target.value)}
                required
                autoComplete="email"
              />
            </div>
            <Button type="submit" className="w-full" disabled={loading}>
              {loading ? 'Küldés...' : 'Jelszó visszaállítás kérése'}
            </Button>
            <p className="text-center text-sm text-gray-600">
              <Link to="/admin/login" className="text-blue-600 hover:underline">
                Vissza a bejelentkezéshez
              </Link>
            </p>
          </form>
        </CardContent>
      </Card>
    </div>
  );
}
