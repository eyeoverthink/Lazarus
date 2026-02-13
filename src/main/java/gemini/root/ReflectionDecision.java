package gemini.root;

/**
 * ReflectionDecision: Decides when to use System-2 Reflector
 * 
 * Rules:
 * - If answer uses no [S#] citations but claims facts → force critique
 * - If query is low-risk (small talk) → fast path
 * - If RAG returns no matches → require "not found" acknowledgment
 */
public class ReflectionDecision {
    
    /**
     * Determine if reflection should be used based on context and query
     */
    public static boolean shouldReflect(String query, String ragContext, boolean userPreference) {
        // User explicitly disabled reflection
        if (!userPreference) {
            return false;
        }
        
        // Low-risk queries (small talk) - fast path
        if (isSmallTalk(query)) {
            return false;
        }
        
        // If RAG returned content, use reflection to ensure proper citation
        if (ragContext != null && !ragContext.isEmpty() && !ragContext.equals("(no matches)")) {
            return true;
        }
        
        // Default to user preference
        return userPreference;
    }
    
    /**
     * Check if answer needs citation validation
     * Returns true if answer makes factual claims without citations
     */
    public static boolean needsCitationCheck(String answer, String ragContext) {
        // If no RAG context was available, citations aren't relevant
        if (ragContext == null || ragContext.isEmpty() || ragContext.equals("(no matches)")) {
            return false;
        }
        
        // Check if answer contains citations
        boolean hasCitations = answer.contains("[S1]") || answer.contains("[S2]") || 
                              answer.contains("[S3]") || answer.contains("[S4]");
        
        // Check if answer makes factual claims (heuristic: contains specific nouns/numbers)
        boolean hasFactualClaims = answer.matches(".*\\b\\d{4}\\b.*") || // years
                                  answer.matches(".*\\b\\d+\\.\\d+\\b.*") || // decimals
                                  answer.length() > 100; // substantial answer
        
        return hasFactualClaims && !hasCitations;
    }
    
    /**
     * Check if query is low-risk small talk
     */
    private static boolean isSmallTalk(String query) {
        String q = query.toLowerCase().trim();
        
        // Greetings
        if (q.matches("^(hi|hello|hey|greetings?|good (morning|afternoon|evening)).*")) {
            return true;
        }
        
        // Simple questions
        if (q.matches("^(how are you|what's up|how('| i)s it going).*")) {
            return true;
        }
        
        // Thanks/acknowledgments
        if (q.matches("^(thanks?( you)?|thank you|thx|ok|okay|got it).*")) {
            return true;
        }
        
        // Very short queries (< 20 chars) are likely casual
        if (q.length() < 20 && !q.contains("?")) {
            return true;
        }
        
        return false;
    }
    
    /**
     * Require "not found" acknowledgment if RAG has no results
     */
    public static String enforceNotFound(String answer, String ragContext) {
        if (ragContext == null || ragContext.isEmpty() || ragContext.equals("(no matches)")) {
            // Check if answer acknowledges lack of context
            String lowerAnswer = answer.toLowerCase();
            if (!lowerAnswer.contains("not found") && 
                !lowerAnswer.contains("no information") &&
                !lowerAnswer.contains("don't have") &&
                !lowerAnswer.contains("cannot find") &&
                !lowerAnswer.contains("not in") &&
                answer.length() > 50) {  // Substantial answer without disclaimer
                
                // Prepend disclaimer
                return "[Note: This answer is based on general knowledge, not from indexed documents]\n\n" + answer;
            }
        }
        return answer;
    }
}
