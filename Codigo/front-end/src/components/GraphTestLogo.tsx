interface GraphTestLogoProps {
  size?: number;
  className?: string;
}

export function GraphTestLogo({ size = 36, className }: GraphTestLogoProps) {
  const w = Math.round(size * (60 / 66));
  return (
    <svg
      width={w}
      height={size}
      viewBox="0 0 60 66"
      fill="none"
      xmlns="http://www.w3.org/2000/svg"
      className={className}
    >
      <defs>
        <linearGradient id="gt-grad" x1="30" y1="5" x2="30" y2="61" gradientUnits="userSpaceOnUse">
          <stop offset="0%"   stopColor="#0891b2" />
          <stop offset="100%" stopColor="#22d3ee" />
        </linearGradient>
      </defs>

      {/* Outer diamond edges */}
      <line x1="30" y1="5"  x2="5"  y2="33" stroke="url(#gt-grad)" strokeWidth="1.8" strokeLinecap="round" />
      <line x1="30" y1="5"  x2="55" y2="33" stroke="url(#gt-grad)" strokeWidth="1.8" strokeLinecap="round" />
      <line x1="5"  y1="33" x2="30" y2="61" stroke="url(#gt-grad)" strokeWidth="1.8" strokeLinecap="round" />
      <line x1="55" y1="33" x2="30" y2="61" stroke="url(#gt-grad)" strokeWidth="1.8" strokeLinecap="round" />

      {/* Internal edges to hub */}
      <line x1="30" y1="5"  x2="30" y2="36" stroke="url(#gt-grad)" strokeWidth="1.8" strokeLinecap="round" />
      <line x1="5"  y1="33" x2="30" y2="36" stroke="url(#gt-grad)" strokeWidth="1.8" strokeLinecap="round" />
      <line x1="55" y1="33" x2="30" y2="36" stroke="url(#gt-grad)" strokeWidth="1.8" strokeLinecap="round" />
      <line x1="30" y1="36" x2="30" y2="61" stroke="url(#gt-grad)" strokeWidth="1.8" strokeLinecap="round" />

      {/* Outer nodes */}
      <circle cx="30" cy="5"  r="3.5" fill="url(#gt-grad)" />
      <circle cx="5"  cy="33" r="3.5" fill="url(#gt-grad)" />
      <circle cx="55" cy="33" r="3.5" fill="url(#gt-grad)" />

      {/* Hub node */}
      <circle cx="30" cy="36" r="4.5" fill="url(#gt-grad)" />

      {/* Bottom focal node */}
      <circle cx="30" cy="61" r="5.5" fill="url(#gt-grad)" />
    </svg>
  );
}
