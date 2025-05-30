package com.example.i18n;

import com.example.common.play.PlayLang; // Placeholder
import com.example.scalalibmodel.Country; // Placeholder
import com.example.scalalibmodel.Language; // Placeholder

import java.time.Duration; // For translateDuration
import java.util.ArrayList; // For translateDuration
import java.util.List;
import java.util.Optional;
// import java.util.stream.Collectors; // Not strictly needed for current impl
// import java.util.stream.Stream; // Not strictly needed for current impl


public final class I18nUtils {
    private I18nUtils() {}

    public static Language toLanguage(PlayLang lang) {
        return new Language(lang.language());
    }

    public static Country toCountry(PlayLang lang) {
        String countryCode = lang.country();
        return countryCode == null ? null : new Country(countryCode);
    }

    public static Language fixJavaLanguage(PlayLang lang) {
        Language l = toLanguage(lang);
        return l.map(val -> "in".equals(val) ? "id" : val);
    }

    public static String translateDuration(java.time.Duration duration, Optional<Boolean> withMinutesOpt, Translate translate) {
        if (translate == null || duration == null) return ""; 
        boolean useMinutes = withMinutesOpt.orElse(duration.toDays() == 0 && duration.toHours() == 0); // Show minutes if days and hours are 0
        
        List<String> parts = new ArrayList<>();
        
        long days = duration.toDays();
        if (days > 0) {
             // Assuming I18nKey.site.nbDays is now accessible
            parts.add(I18nKey.site.nbDays.pluralSameTxt(translate, days));
        }
        
        long hours = duration.toHours() % 24;
        // Show hours if > 0, or if it's the largest unit being shown (no days, and not only minutes if days=0)
        if (hours > 0 || (days == 0 && (!useMinutes || parts.isEmpty()))) {
            parts.add(I18nKey.site.nbHours.pluralSameTxt(translate, hours));
        }
        
        if (useMinutes) {
            long minutes = duration.toMinutes() % 60;
            // Show minutes if > 0, or if it's the only unit intended to be shown (e.g. duration < 1 hour)
            // or if parts is empty (e.g. duration is 0, show "0 minutes")
            if (minutes > 0 || (parts.isEmpty() && days == 0 && hours == 0)) { 
                parts.add(I18nKey.site.nbMinutes.pluralSameTxt(translate, minutes));
            }
        }
        
        if (parts.isEmpty()) { 
            // If duration was very short (e.g. seconds) and useMinutes was false, or all components were zero.
            // Default to showing "0 minutes" if useMinutes was true, or "0 hours" if hours could have been shown, etc.
            // Or, if duration is truly zero, what should be shown? Let's default to "0 minutes" if useMinutes was intended.
            if (useMinutes || (duration.getSeconds() < 60 && duration.getSeconds() >=0 ) ) { // Show 0 minutes for sub-minute durations if useMinutes is true
                 return I18nKey.site.nbMinutes.pluralSameTxt(translate, 0);
            } else if (duration.toHours() < 24 && duration.toHours() >= 0 && days == 0) { // if it was a duration of 0 hours
                 return I18nKey.site.nbHours.pluralSameTxt(translate, 0);
            } else if (days == 0) { // if it was a duration of 0 days
                 return I18nKey.site.nbDays.pluralSameTxt(translate, 0);
            }
            // If all else fails (e.g. negative duration or unhandled zero case), return empty or a default.
            return I18nKey.site.nbMinutes.pluralSameTxt(translate, 0); // Fallback for very small or zero durations.
        }

        return String.join(", ", parts);
    }

    public static String translateDuration(java.time.Duration duration, Translate translate) {
        return translateDuration(duration, Optional.empty(), translate);
    }
    
    // Placeholder for FiniteDuration if it becomes a separate class.
    // For now, assuming java.time.Duration is used.
    // public static String translateFiniteDuration(com.example.common.FiniteDuration finiteDuration, Optional<Boolean> withMinutes, Translate translate) {
    //    return translateDuration(java.time.Duration.ofSeconds(finiteDuration.toSeconds()), withMinutes, translate);
    // }
}
