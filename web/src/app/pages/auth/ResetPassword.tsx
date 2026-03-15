import { useState, useEffect } from 'react';
import { Link, useNavigate, useSearchParams } from 'react-router';
import { Button } from '../../components/ui/button';
import { Input } from '../../components/ui/input';
import { Label } from '../../components/ui/label';
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '../../components/ui/card';
import { useAuth } from '../../context/AuthContext';
import { Alert, AlertDescription } from '../../components/ui/alert';

export function ResetPassword() {
  const { resetPassword } = useAuth();
  const navigate = useNavigate();
  const [searchParams] = useSearchParams();
  const tokenFromUrl = searchParams.get('token') ?? '';
  const [token, setToken] = useState(tokenFromUrl);
  const [newPassword, setNewPassword] = useState('');
  const [confirmPassword, setConfirmPassword] = useState('');
  const [error, setError] = useState('');
  const [success, setSuccess] = useState(false);
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    if (tokenFromUrl) setToken(tokenFromUrl);
  }, [tokenFromUrl]);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError('');
    if (newPassword !== confirmPassword) {
      setError('A két jelszó nem egyezik.');
      return;
    }
    if (!token.trim()) {
      setError('Hiányzó vagy érvénytelen visszaállítási link.');
      return;
    }
    setLoading(true);
    try {
      await resetPassword(token, newPassword);
      setSuccess(true);
      setTimeout(() => navigate('/admin/login', { replace: true }), 2000);
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Jelszó visszaállítás sikertelen');
    } finally {
      setLoading(false);
    }
  };

  if (success) {
    return (
      <div className="min-h-screen flex items-center justify-center bg-gray-50 px-4">
        <Card className="w-full max-w-md">
          <CardContent className="pt-6">
            <Alert>
              <AlertDescription>Jelszó sikeresen megváltoztatva. Átirányítás a bejelentkezéshez...</AlertDescription>
            </Alert>
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
          <CardTitle className="text-2xl">Új jelszó</CardTitle>
          <CardDescription>Add meg az új jelszavad. A token az e-mailben vagy az előző lépésben érkezett.</CardDescription>
        </CardHeader>
        <CardContent>
          <form onSubmit={handleSubmit} className="space-y-4">
            {error && (
              <Alert variant="destructive">
                <AlertDescription>{error}</AlertDescription>
              </Alert>
            )}
            {!tokenFromUrl && (
              <div className="space-y-2">
                <Label htmlFor="token">Visszaállítási token</Label>
                <Input
                  id="token"
                  type="text"
                  placeholder="Token az e-mailből"
                  value={token}
                  onChange={(e) => setToken(e.target.value)}
                />
              </div>
            )}
            <div className="space-y-2">
              <Label htmlFor="newPassword">Új jelszó</Label>
              <Input
                id="newPassword"
                type="password"
                value={newPassword}
                onChange={(e) => setNewPassword(e.target.value)}
                required
                minLength={6}
                autoComplete="new-password"
              />
            </div>
            <div className="space-y-2">
              <Label htmlFor="confirmPassword">Jelszó megerősítése</Label>
              <Input
                id="confirmPassword"
                type="password"
                value={confirmPassword}
                onChange={(e) => setConfirmPassword(e.target.value)}
                required
                minLength={6}
                autoComplete="new-password"
              />
            </div>
            <Button type="submit" className="w-full" disabled={loading}>
              {loading ? 'Mentés...' : 'Jelszó mentése'}
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
